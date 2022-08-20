package com.example.test1;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RegisterListRecyclerViewAdapter extends RecyclerView.Adapter<RegisterListRecyclerViewAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    private ArrayList<RegisterListData> mData = null;

    /* 리스트의 한 아이템을 담는 뷰홀더 객체 정의 */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgview;
        TextView text_reg_list_title;
        TextView text_reg_list_date;
        TextView text_reg_list_count;
        TextView text_reg_list_loc;


        ViewHolder(View itemView) {
            super(itemView);

            GradientDrawable drawable = (GradientDrawable)itemView.getContext().getDrawable(R.drawable.background_rounding);
            imgview = itemView.findViewById(R.id.img_reg_list);
            text_reg_list_title = itemView.findViewById(R.id.text_reg_list_title);
            text_reg_list_date = itemView.findViewById(R.id.text_reg_list_date);
            text_reg_list_loc = itemView.findViewById(R.id.text_reg_list_loc);
            text_reg_list_count = itemView.findViewById(R.id.text_reg_list_count);

            imgview.setBackground(drawable);
            imgview.setClipToOutline(true);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);

                        }
                    }
                }
            });
        }
    }

    RegisterListRecyclerViewAdapter(ArrayList<RegisterListData> list) {
        mData = list;
    }


    /* 미리 만들어놓은 아이템 레이아웃을 레이아웃 인플레이터를 통해 뷰에 인플레이트하고 해당 뷰를 파라미터로 하여 뷰홀더 객체 생성 */
    @NonNull
    @Override
    public RegisterListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_registerlist, parent, false);
        RegisterListRecyclerViewAdapter.ViewHolder vh = new RegisterListRecyclerViewAdapter.ViewHolder(view);
        return vh;
    }

    /* 아이템의 내용을 설정 */
    @Override
    public void onBindViewHolder(@NonNull RegisterListRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.imgview.setImageResource(mData.get(position).imgsrc);
        holder.text_reg_list_title.setText(mData.get(position).title);
        holder.text_reg_list_date.setText(mData.get(position).date);
        holder.text_reg_list_loc.setText(mData.get(position).loc);
        holder.text_reg_list_count.setText(mData.get(position).cur_count + "/" + mData.get(position).max_count);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
