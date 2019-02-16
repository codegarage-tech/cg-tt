package tech.codegarage.tidetwist.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import tech.codegarage.tidetwist.model.Order;
import tech.codegarage.tidetwist.viewholder.OrderListViewHolder;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class OrderListAdapter extends RecyclerArrayAdapter<Order> {

    private static final int VIEW_TYPE_REGULAR = 1;

    public OrderListAdapter(Context context) {
        super(context);
    }

    @Override
    public int getViewType(int position) {
        return VIEW_TYPE_REGULAR;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_REGULAR:
                return new OrderListViewHolder(parent);
            default:
                throw new InvalidParameterException();
        }
    }

    public void updateItem(Order order) {
        int position = getItemPosition(order);

        if (position != -1) {
            List<Order> orders = new ArrayList<>();
            orders.addAll(getAllData());
            orders.remove(position);
            orders.add(position, order);

            removeAll();
            addAll(orders);
        } else {
            insert(order, 0);
        }
        notifyDataSetChanged();
    }

    private int getItemPosition(Order order) {
        List<Order> orders = getAllData();
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrder_id().equalsIgnoreCase(order.getOrder_id())) {
                return i;
            }
        }
        return -1;
    }
}