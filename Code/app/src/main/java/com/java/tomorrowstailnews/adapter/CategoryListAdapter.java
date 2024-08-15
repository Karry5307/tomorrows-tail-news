package com.java.tomorrowstailnews.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.java.tomorrowstailnews.R;
import com.java.tomorrowstailnews.entity.CategoryInfo;

import java.util.ArrayList;
import java.util.List;

public class CategoryListAdapter extends BaseAdapter {
    private List<CategoryInfo> dataList = new ArrayList<>();
    private int colorGray, colorBlue;

    public CategoryListAdapter(List<CategoryInfo> dataList) {
        this.dataList = getShallowCopy(dataList);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        CategoryListHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_categories_item, null);
            holder = new CategoryListHolder();
            holder.textView = view.findViewById(R.id.textCategoriesItem);
            view.setTag(holder);
        } else {
            holder = (CategoryListHolder) view.getTag();
        }
        colorGray = view.getResources().getColor(R.color.gray);
        colorBlue = view.getResources().getColor(R.color.mediumBlue);
        holder.textView.setText(dataList.get(position).getCategory());
        holder.textView.setTextColor(dataList.get(position).getIsVisible() == 1 ? colorBlue : colorGray);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (CategoryInfo item : dataList) {
                    if (item.getCategory() == dataList.get(position).getCategory()) {
                        item.setIsVisible(item.getIsVisible() ^ 1);
                        break;
                    }
                }
                notifyDataSetChanged();
            }
        });
        return view;
    }

    public List<CategoryInfo> getDataList() {
        return getShallowCopy(dataList);
    }

    public void setDataList(List<CategoryInfo> dataList) {
        this.dataList = getShallowCopy(dataList);
        notifyDataSetChanged();
    }

    public List<CategoryInfo> getShallowCopy(List<CategoryInfo> dataList) {
        List<CategoryInfo> resultList = new ArrayList<>();
        for (CategoryInfo item : dataList) {
            resultList.add(new CategoryInfo(item.getCategory(), item.getIsVisible()));
        }
        return resultList;
    }

    public static class CategoryListHolder {
        TextView textView;
    }
}
