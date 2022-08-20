package com.example.test1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private ArrayList<ListData> mData = null;

    /* 리스트의 한 아이템을 담는 뷰홀더 객체 정의 */
    public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgview;
            TextView text_title, text_type, text_date, text_headcount;

        ViewHolder(View itemView) {
            super(itemView);

            imgview = itemView.findViewById(R.id.img_list_user);
            text_title = itemView.findViewById(R.id.text_list_title);
            text_type = itemView.findViewById(R.id.text_list_type);
            text_date = itemView.findViewById(R.id.text_list_date);
            text_headcount = itemView.findViewById(R.id.text_list_headcount);
        }
    }

    MyRecyclerViewAdapter(ArrayList<ListData> list) {
        mData = list;
    }


    /* 미리 만들어놓은 아이템 레이아웃을 레이아웃 인플레이터를 통해 뷰에 인플레이트하고 해당 뷰를 파라미터로 하여 뷰홀더 객체 생성 */
    @NonNull
    @Override
    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.list_user_register, parent, false);
        MyRecyclerViewAdapter.ViewHolder vh = new MyRecyclerViewAdapter.ViewHolder(view);
        return vh;
    }

    /* 아이템의 내용을 설정 */
    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewAdapter.ViewHolder holder, int position) {
        String strTitle = mData.get(position).title;
        holder.imgview.setImageResource(R.drawable.img_user_icon);
//        holder.text1.setText(text1);
//        holder.text2.setText(text2);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
