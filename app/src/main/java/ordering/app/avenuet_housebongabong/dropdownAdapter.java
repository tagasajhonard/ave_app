package ordering.app.avenuet_housebongabong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class dropdownAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private List<String> mItems;
    private int mResource;

    public dropdownAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        mContext = context;
        mResource = resource;
        mItems = items;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(mItems.get(position));
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(mItems.get(position));
        return convertView;
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.dropdown, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.textViewItem);
        textView.setText(mItems.get(position));

        return convertView;
    }
}
