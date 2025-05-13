package com.example.admin.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.R;
import com.example.admin.adapter.OrderAdapter;
import com.example.admin.helper.ToastHelper;
import com.example.admin.helper.firebase.OrderRepository;
import com.example.admin.model.Order;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PesananFragment extends Fragment  {

    private final Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable debounceRunnable;
    private ToastHelper orderToast;

    private  OrderAdapter orderAdapter;

    // Daftar kategori produk
    private final List<String> categories = Arrays.asList(
            "Banner & Spanduk",
            "Stiker & Cutting",
            "Media Promosi",
            "Percetakan & Offset",
            "Aksesoris"
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderToast = new ToastHelper(requireContext());

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pesanan, container, false);

        // Inisialisasi FAB
        FloatingActionButton fabAdd = view.findViewById(R.id.btn_add_order);
        fabAdd.setOnClickListener(v -> debounce(this::showPesananModal, 500));


        // Dummy data untuk testing
        List<Order> dummyOrders = new ArrayList<>();

        // Inisialisasi RecyclerView
        RecyclerView orderViews = view.findViewById(R.id.rv_order_list);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(orderViews.getContext(),
                DividerItemDecoration.VERTICAL);
        orderViews.addItemDecoration(dividerItemDecoration);
        orderViews.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Buat adapter dan set ke RecyclerView
        orderAdapter = new OrderAdapter(dummyOrders);
        orderViews.setAdapter(orderAdapter);

        loadOrders();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        new OrderRepository().getAllOrders(new OrderRepository.FirestoreCallback<List<Order>>() {
            @Override
            public void onSuccess(List<Order> orders) {
                orders.sort(Comparator.comparing(Order::getOrderDate).reversed());
                orderAdapter.updateList(orders);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(requireContext(), "Gagal memuat pesanan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showPesananModal() {
        List<String> categories = Arrays.asList(
                "Banner & Spanduk",
                "Stiker & Cutting",
                "Media Promosi",
                "Percetakan & Offset",
                "Perlengkapan Event",
                "Aksesoris"
        );


        try {
            BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
            View modalView = LayoutInflater.from(requireContext()).inflate(R.layout.modal_pesanan, null);
            dialog.setContentView(modalView);

            // Inisialisasi komponen input
            TextInputEditText etCustomerName = modalView.findViewById(R.id.et_customer_name);
            TextInputEditText etWhatsapp = modalView.findViewById(R.id.et_whatsapp);
            TextInputEditText etOrderDetails = modalView.findViewById(R.id.et_order_details);
            TextInputEditText etTotalPrice = modalView.findViewById(R.id.et_total_price);

            Spinner dropdownKategori = modalView.findViewById(R.id.sp_kategori);

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(
                    requireContext(),
                    R.layout.dropdown_menu_item,
                    categories
            );
            dropdownKategori.setAdapter(dataAdapter);

            Button btnConfirm = modalView.findViewById(R.id.btn_confirm);
            Button btnCancel = modalView.findViewById(R.id.btn_cancel);

            btnConfirm.setOnClickListener(v -> {
                // Get selected category from spinner
                String selectedCategory = dropdownKategori.getSelectedItem().toString();

                handleOrderSubmission(
                        etCustomerName,
                        etWhatsapp,
                        etOrderDetails,
                        selectedCategory,  // Pass selected spinner value here
                        etTotalPrice,
                        dialog
                );
            });

            btnCancel.setOnClickListener(v -> dialog.dismiss());

            dialog.show();

        } catch (Exception e) {
            Log.e("ModalError", "Gagal membuka modal: " + e.getMessage());
            Toast.makeText(requireContext(), "Gagal membuka form pesanan", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleOrderSubmission(
            @NonNull TextInputEditText etCustomerName,
            @NonNull TextInputEditText etWhatsapp,
            @NonNull TextInputEditText etOrderDetails,
            String selectedCategory,
            @NonNull TextInputEditText etTotalPrice,
            BottomSheetDialog dialog
    ) {
        // Validasi input
        String customerName = etCustomerName.getText().toString().trim();
        String whatsapp = etWhatsapp.getText().toString().trim();
        String orderDetails = etOrderDetails.getText().toString().trim();
        String priceStr = etTotalPrice.getText().toString().trim();

        if (customerName.isEmpty() || whatsapp.isEmpty() || orderDetails.isEmpty() ||
                selectedCategory.isEmpty() || priceStr.isEmpty()) {
            orderToast.showToast("Harap isi semua field yang wajib!");
            return;
        }

        if (!categories.contains(selectedCategory)) {
            orderToast.showToast("Pilih kategori yang valid!");
            return;
        }

        try {
            double totalPrice = Double.parseDouble(priceStr);
            Order newOrder = new Order(
                    customerName,
                    whatsapp,
                    orderDetails,
                    selectedCategory,
                    totalPrice
            );

            Log.d("OrderInfo", newOrder.toString());

            new OrderRepository().addOrder(newOrder, new OrderRepository.FirestoreCallback<String>() {
                @Override
                public void onSuccess(String orderId) {
                    requireActivity().runOnUiThread(() -> {
                        orderToast.showToast("Pesanan berhasil disimpan!\nID: " + orderId);
                        loadOrders();
                        dialog.dismiss();
                    });
                }

                @Override
                public void onError(Exception e) {
                    requireActivity().runOnUiThread(() -> {
                        orderToast.showToast("Gagal menyimpan: " + e.getMessage());
                        Log.e("FirestoreError", "Error: ", e);
                    });
                }
            });
        } catch (NumberFormatException e) {
            orderToast.showToast("Format harga tidak valid!");
        }
    }

    private void debounce(Runnable action, long delayMillis) {
        if (debounceRunnable != null) {
            debounceHandler.removeCallbacks(debounceRunnable);
        }
        debounceRunnable = () -> {
            action.run();
            debounceRunnable = null;
        };
        debounceHandler.postDelayed(debounceRunnable, delayMillis);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Membersihkan handler untuk mencegah memory leak
        debounceHandler.removeCallbacksAndMessages(null);
    }
}