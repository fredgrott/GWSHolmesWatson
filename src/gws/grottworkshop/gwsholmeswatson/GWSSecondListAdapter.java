package gws.grottworkshop.gwsholmeswatson;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;

import com.actionbarsherlock.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Borrowed from fragment booststrap, hnilsen
 * @author fredgrott
 *
 */
public class GWSSecondListAdapter extends BaseAdapter {

	private Context context;
    String[] values = {};

    public GWSSecondListAdapter(int inflated_array, Context context) {
        this.context = context;
        values = context.getResources().getStringArray(inflated_array);
    }

    public int getCount() {
        return values.length;
    }

    public Object getItem(int position) {
        return values[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout listView;
        listView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.fbs_listitem_second, parent, false);

        TextView itemText = (TextView)listView.findViewById(R.id.secondlist_item_text);
        itemText.setText(values[position]);

        return listView;
    }
}
