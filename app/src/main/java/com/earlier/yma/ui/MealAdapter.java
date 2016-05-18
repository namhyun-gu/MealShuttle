package com.earlier.yma.ui;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.earlier.yma.R;
import com.earlier.yma.data.model.item.Item;
import com.earlier.yma.data.model.item.meal.DefaultItem;
import com.earlier.yma.data.model.item.meal.GraphItem;
import com.earlier.yma.data.model.item.meal.KcalItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by namhyun on 2015-11-26.
 */
public class MealAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_DIVIDER = 1;
    private static final int TYPE_NUTRIENT = 2;
    private static final int TYPE_KCAL = 3;

    private final LayoutInflater layoutInflater;
    private List<Item> itemList = new ArrayList<>();

    public MealAdapter(Activity hostActivity) {
        this.layoutInflater = LayoutInflater.from(hostActivity);
    }

    public void addItems(List<Item> items) {
        for (Item item : items)
            itemList.add(item);
        notifyDataSetChanged();
    }

    public void clearItem() {
        itemList.clear();
        notifyDataSetChanged();
    }

    public List<Item> getItems() {
        return itemList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_DEFAULT:
                return createDefaultHolder(parent);
            case TYPE_DIVIDER:
                return createDividerHolder(parent);
            case TYPE_NUTRIENT:
                return createGraphHolder(parent);
            case TYPE_KCAL:
                return createKcalHolder(parent);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Item item = itemList.get(position);
        switch (getItemViewType(position)) {
            case TYPE_DEFAULT:
                DefaultItem defaultItem = (DefaultItem) item;
                DefaultHolder defaultHolder = (DefaultHolder) holder;
                if (defaultItem.getSummary() == null)
                    defaultHolder.summary.setVisibility(View.GONE);
                defaultHolder.title.setText(defaultItem.getTitle());
                defaultHolder.summary.setText(defaultItem.getSummary());
                break;
            case TYPE_NUTRIENT:
                GraphItem graphItem = (GraphItem) item;
                GraphHolder graphHolder = (GraphHolder) holder;
                for (int index = 0; index < graphItem.getSummarys().length; index++) {
                    String value = graphItem.getSummarys()[index] + " g";
                    graphHolder.summarys.get(index).setText(value);
                }
                break;
            case TYPE_KCAL:
                KcalItem kcalItem = (KcalItem) item;
                String value = kcalItem.getSummary() + " kcal";
                ((KcalHolder) holder).summary.setText(value);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_NUTRIENT;
            case 1:
                return TYPE_KCAL;
            case 2:
                return TYPE_DIVIDER;
            default:
                return TYPE_DEFAULT;
        }
    }

    @NonNull
    private DefaultHolder createDefaultHolder(ViewGroup parent) {
        final DefaultHolder holder = new DefaultHolder(layoutInflater.inflate(
                R.layout.item_default, parent, false
        ));
        return holder;
    }

    @NonNull
    private DividerHolder createDividerHolder(ViewGroup parent) {
        final DividerHolder holder = new DividerHolder(layoutInflater.inflate(
                R.layout.item_divider, parent, false
        ));
        return holder;
    }

    @NonNull
    private GraphHolder createGraphHolder(ViewGroup parent) {
        final GraphHolder holder = new GraphHolder(layoutInflater.inflate(
                R.layout.item_graph, parent, false
        ));
        return holder;
    }

    @NonNull
    private KcalHolder createKcalHolder(ViewGroup parent) {
        final KcalHolder holder = new KcalHolder(layoutInflater.inflate(
                R.layout.item_kcal, parent, false
        ));
        return holder;
    }

    public class DefaultHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.default_title)
        TextView title;
        @Bind(R.id.default_summary)
        TextView summary;

        public DefaultHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class DividerHolder extends RecyclerView.ViewHolder {

        public DividerHolder(View itemView) {
            super(itemView);
        }
    }

    public class GraphHolder extends RecyclerView.ViewHolder {
        public List<TextView> summarys = new ArrayList<>();

        public GraphHolder(View itemView) {
            super(itemView);
            summarys.add((TextView) itemView.findViewById(R.id.nutrient_summary_1));
            summarys.add((TextView) itemView.findViewById(R.id.nutrient_summary_2));
            summarys.add((TextView) itemView.findViewById(R.id.nutrient_summary_3));
        }
    }

    public class KcalHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.default_summary)
        TextView summary;

        public KcalHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
