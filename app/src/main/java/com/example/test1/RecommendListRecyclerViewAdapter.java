package com.example.test1;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naver.maps.map.MapFragment;

import java.util.ArrayList;

public class RecommendListRecyclerViewAdapter extends RecyclerView.Adapter<RecommendListRecyclerViewAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener mListener = null;
    private ArrayList<RecommendListData> mData = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    /* 리스트의 한 아이템을 담는 뷰홀더 객체 정의 */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgview;
        TextView text_title;


        ViewHolder(View itemView) {
            super(itemView);

            GradientDrawable drawable = (GradientDrawable)itemView.getContext().getDrawable(R.drawable.background_rounding);
            imgview = itemView.findViewById(R.id.img_recommend);
            text_title = itemView.findViewById(R.id.text_recommend);

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

    RecommendListRecyclerViewAdapter(ArrayList<RecommendListData> list) {
        mData = list;
    }


    /* 미리 만들어놓은 아이템 레이아웃을 레이아웃 인플레이터를 통해 뷰에 인플레이트하고 해당 뷰를 파라미터로 하여 뷰홀더 객체 생성 */
    @NonNull
    @Override
    public RecommendListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.list_recommend, parent, false);
        RecommendListRecyclerViewAdapter.ViewHolder vh = new RecommendListRecyclerViewAdapter.ViewHolder(view);
        return vh;
    }

    /* 아이템의 내용을 설정 */
    @Override
    public void onBindViewHolder(@NonNull RecommendListRecyclerViewAdapter.ViewHolder holder, int position) {
        String strTitle = mData.get(position).getTitle();
        int img_src = mData.get(position).getImgSrc();
        holder.imgview.setImageResource(img_src);
        holder.text_title.setText(strTitle);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
