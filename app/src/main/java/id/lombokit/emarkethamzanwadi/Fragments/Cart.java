package id.lombokit.emarkethamzanwadi.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.lombokit.emarkethamzanwadi.Models.Categories;
import id.lombokit.emarkethamzanwadi.Models.List_pesanan;
import id.lombokit.emarkethamzanwadi.Models.Pesanan;

import id.lombokit.emarkethamzanwadi.Models.Products;
import id.lombokit.emarkethamzanwadi.R;
import id.lombokit.emarkethamzanwadi.SessionManager.SessionManager;


public class Cart extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private LinearLayout linear_progressbar;

    private Toolbar toolbar;
    private TextView toolBarTxt;
    SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;
    private RecycleAdapter_AddProduct mAdapter;
    private int status_code;
    private String token, totalPriceOfProducts;
    String url = "http://192.168.43.153/e_comm_covid/pesanan.php";
    private TextView quantityOfTotalProduct, priceOfTotalProduct, next;
    private List_pesanan list_pesanan;
    SessionManager sessionManager;
    TextView textViewTotal;




    private View view;


    Animation startAnimation;


    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        startAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);

        initComponent(view);
        sessionManager = new SessionManager(getContext());


        swipeRefreshLayout = view.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (swipeRefreshLayout != null){
                    swipeRefreshLayout.setRefreshing(true);
                }
                loadData();

            }
        });


        list_pesanan = new List_pesanan();
        list_pesanan.pesananArrayList = new ArrayList<>();



        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new RecycleAdapter_AddProduct(getActivity(), list_pesanan);
        return view;
    }
    private void initComponent(View view) {

    }


    @Override
    public void onRefresh() {
    loadData();
    }
    private  void  loadData() {
        swipeRefreshLayout.setRefreshing(true);
        if (list_pesanan.pesananArrayList != null) {
            list_pesanan.pesananArrayList.clear();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);
                            String nama_barang = data.getString("nama_barang");
                            String harga = data.getString("harga");
                            String gambar = data.getString("gambar");
                            String toko = data.getString("nama");
                            String stok = data.getString("stok");
                            Pesanan productsm = new Pesanan(
                                    nama_barang,
                                    harga,
                                    gambar,
                                    toko,
                                    stok
                            );

                            list_pesanan.pesananArrayList.add(productsm);
                            //Toast.makeText(getContext(),""+nama,Toast.LENGTH_SHORT).show();

                        }

                        recyclerView.setAdapter(mAdapter);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    swipeRefreshLayout.setRefreshing(false);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    swipeRefreshLayout.setRefreshing(false);

                }
            });
            Volley.newRequestQueue(getContext()).add(stringRequest);
        }
    }

    public class RecycleAdapter_AddProduct extends RecyclerView.Adapter<RecycleAdapter_AddProduct.MyViewHolder> {

        Context context;
        boolean showingFirst = true;
        private List_pesanan list_pesanan;
        //private Products products;
        int recentPos = -1;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView image;
            TextView title;
            TextView price;
            TextView quantityTxt;
            TextView textViewtoko;
            ImageView clik_add;
            private LinearLayout llMinus, llPlus;
            int quantity;


            public MyViewHolder(View view) {
                super(view);
                image = (ImageView) view.findViewById(R.id.image);
                clik_add = view.findViewById(R.id.add_cart);
                title = (TextView) view.findViewById(R.id.title);
                price = (TextView) view.findViewById(R.id.price);
                quantityTxt = (TextView) view.findViewById(R.id.quantityTxt);
                textViewtoko = view.findViewById(R.id.toko);
                llPlus = (LinearLayout) view.findViewById(R.id.llPlus);
                llMinus = (LinearLayout) view.findViewById(R.id.llMinus);
            }
        }


        public RecycleAdapter_AddProduct(Context context, List_pesanan list_pesanan) {
            this .list_pesanan = list_pesanan;
            this.context = context;
        }

        @Override
        public RecycleAdapter_AddProduct.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cart, parent, false);

            return new RecycleAdapter_AddProduct.MyViewHolder(itemView);
        }


        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onBindViewHolder(final RecycleAdapter_AddProduct.MyViewHolder holder, final int position) {
//            Products movie = productsList.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getContext(), ""+categories.getProductsArrayList().get(position).getId_barang(), Toast.LENGTH_SHORT).show();
                }
            });

            holder.title.setText(list_pesanan.getPesananArrayList().get(position).getNama_barang());
            holder.price.setText(list_pesanan.getPesananArrayList().get(position).getHarga());
            //holder.quantityTxt.setText(categories.getProductsArrayList().get(position).getQuantity() + "");
            holder.textViewtoko.setText(list_pesanan.getPesananArrayList().get(position).getNama());
            Glide.with(getContext()).load("http://192.168.43.153/e_comm_covid/assets/img/"+ list_pesanan.getPesananArrayList().get(position).getGambar()).into(holder.image);
            //holder.image.setImageDrawable(getResources().getDrawable(categories.getProductsArrayList().get(position).getImage()));

            //holder.quantity = categories.getProductsArrayList().get(position).getQuantity();
            //int totalPrice = holder.quantity * Integer.parseInt(categories.getProductsArrayList().get(position).getPrice());
            if (position == recentPos) {
                Log.e("pos", "" + recentPos);
                // start animation
                holder.quantityTxt.startAnimation(startAnimation);
            } else {
                holder.quantityTxt.clearAnimation();

            }

            if (holder.quantity > 0) {
                holder.quantityTxt.setVisibility(View.VISIBLE);
                holder.llMinus.setVisibility(View.VISIBLE);
            } else {
                holder.quantityTxt.setVisibility(View.GONE);
                holder.llMinus.setVisibility(View.GONE);
            }


            //categories.getProductsArrayList().get(position).setPriceAsPerQuantity("" + totalPrice);


            holder.llPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String stok_barang = list_pesanan.getPesananArrayList().get(position).getStok();
                    int covert_stok = Integer.parseInt(stok_barang);

                    if (holder.quantity < covert_stok) {

                        recentPos = position;
                        holder.quantity = holder.quantity + 1;
                        //categories.getProductsArrayList().get(position).setQuantity(holder.quantity);
                        //categories.getProductsArrayList().get(position).setPriceAsPerQuantity("" + holder.quantity * Integer.parseInt(categories.getProductsArrayList().get(position).getPrice()));
                        holder.quantityTxt.setText("" + holder.quantity);
                        String qty=holder.quantityTxt.getText().toString();
                        String hrg = list_pesanan.getPesananArrayList().get(position).getHarga();
                        int c_qty = Integer.parseInt(qty);
                        int c_hrg = Integer.parseInt(hrg);

                        int total_belanja =0;
                        for (int i=0;i<list_pesanan.getPesananArrayList().size();i++){
                            total_belanja = (c_qty*c_hrg)+(c_qty*c_hrg);
                        }

                        String s_total = String.valueOf(total_belanja);
                        textViewTotal = getActivity().findViewById(R.id.value);
                        textViewTotal.setText(s_total);
                    }
                    notifyDataSetChanged();


                }
            });
            holder.llMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String stok_barang = list_pesanan.getPesananArrayList().get(position).getStok();
                    int covert_stok = Integer.parseInt(stok_barang);
                    if (holder.quantity > 0 && holder.quantity <=covert_stok) {

                        recentPos = position;

                        holder.quantity = holder.quantity - 1;
                        //categories.getProductsArrayList().get(position).setQuantity(holder.quantity);
                        //categories.getProductsArrayList().get(position).setPriceAsPerQuantity("" + holder.quantity * Integer.parseInt(categories.getProductsArrayList().get(position).getPrice()));

                        holder.quantityTxt.setText("" + holder.quantity);
                        String qty=holder.quantityTxt.getText().toString();
                        String hrg = list_pesanan.getPesananArrayList().get(position).getHarga();
                        int c_qty = Integer.parseInt(qty);
                        int c_hrg = Integer.parseInt(hrg);
                        int total_belanja =c_qty*c_hrg;
                        String s_total = String.valueOf(total_belanja);
                        textViewTotal = getActivity().findViewById(R.id.value);
                        textViewTotal.setText(s_total);



                    }

                    notifyDataSetChanged();

                }
            });



        }

        @Override
        public int getItemCount() {
            return list_pesanan.getPesananArrayList().size();
        }

    }

}
