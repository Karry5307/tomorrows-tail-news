package com.java.tomorrowstailnews;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.java.tomorrowstailnews.adapter.CategoryListAdapter;
import com.java.tomorrowstailnews.entity.CategoryInfo;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment{
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ImageView imgSearch;
    private TextView textView;
    private GridView gridView;
    private CategoryListAdapter categoryListAdapter;

    private final String categories[] = {"全部", "科技", "社会", "文化", "军事", "体育", "娱乐", "健康", "财经", "汽车", "教育"};
    private List<CategoryInfo> categoryList = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public int getLength() {
        int length = 0;
        for (CategoryInfo item : categoryList) {
            length += item.getIsVisible();
        }
        return length;
    }

    public CategoryInfo getItem(int position) {
        int currentIndex = 0;
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getIsVisible() == 1) {
                if (currentIndex == position) {
                    return categoryList.get(i);
                } else {
                    currentIndex++;
                }
            }
        }
        return new CategoryInfo("", -1);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbarHomeFragment);
        tabLayout = view.findViewById(R.id.tabLayoutCategory);
        viewPager2 = view.findViewById(R.id.viewPager2Category);
        imgSearch = view.findViewById(R.id.imgSearchHomeFragment);
        textView = view.findViewById(R.id.textSearchHomeFragment);

        for (String category: categories) {
            categoryList.add(new CategoryInfo(category, 1));
        }
        viewPager2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return CategorizedNewsFragment.newInstance(getItem(position).getCategory());
            }

            @Override
            public int getItemCount() {
                return getLength();
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(getItem(position).getCategory());
            }
        });
        tabLayoutMediator.attach();
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                intent.putExtra("keyword", getContext().getString(R.string.searchSuggestion));
                startActivity(intent);
            }
        });
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View inflatedView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_modify_categories, null);
                gridView = inflatedView.findViewById(R.id.gridViewCategories);
                gridView.setAdapter(categoryListAdapter = new CategoryListAdapter(categoryList));
                new AlertDialog.Builder(getActivity()).setTitle("修改新闻列表").setView(inflatedView)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                categoryListAdapter.setDataList(categoryList);
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                categoryList = categoryListAdapter.getDataList();
                                TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
                                    @Override
                                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                                        tab.setText(getItem(position).getCategory());
                                    }
                                });
                                tabLayoutMediator.attach();
                            }
                        }).show();
            }
        });
    }
}