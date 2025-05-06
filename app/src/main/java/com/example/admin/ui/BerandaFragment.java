package com.example.admin.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.CustomerDetailActivity;
import com.example.admin.ProductManagementActivity;
import com.example.admin.R;
import com.example.admin.TransactionManagementActivity;
import com.example.admin.helper.ToastHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BerandaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BerandaFragment extends Fragment {

    public ToastHelper berandaToast;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public BerandaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BerandaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BerandaFragment newInstance(String param1, String param2) {
        BerandaFragment fragment = new BerandaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        berandaToast = new ToastHelper(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beranda, container, false);


//        Detail Total Pelanggan
        TextView tvDetailPelanggan = view.findViewById(R.id.tv_detail_pelanggan);
        tvDetailPelanggan.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CustomerDetailActivity.class);
            startActivity(intent);
        });

//        Detail manajemen Transaksi
        TextView tvDetailTransaksi = view.findViewById(R.id.tv_detail_transaksi);
        tvDetailTransaksi.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), TransactionManagementActivity.class);
            startActivity(intent);
        });

//        Detail manajemen Produk
        TextView tvDetailProduk = view.findViewById(R.id.tv_detail_produk);
        tvDetailProduk.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ProductManagementActivity.class);
            startActivity(intent);
        });

//        Lihat Semua Produk Terlaris
        TextView tvLihatSemua = view.findViewById(R.id.tv_lihat_semua);
        tvLihatSemua.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ProductManagementActivity.class);
            startActivity(intent);
        });

        return view;
    }

}