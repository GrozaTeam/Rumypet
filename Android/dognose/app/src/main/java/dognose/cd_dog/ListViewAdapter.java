package dognose.cd_dog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by paeng on 2018. 4. 8..
 */

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;

    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listview_item_doglist, parent, false);

        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.img_dog);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.tv_name);
        TextView speciesTextView = (TextView) convertView.findViewById(R.id.tv_species);
        TextView genderTextView = (TextView) convertView.findViewById(R.id.tv_gender);
        TextView ageTextView2 = (TextView) convertView.findViewById(R.id.tv_age);

        ListViewItem listViewItem = listViewItemList.get(position);


        iconImageView.setImageDrawable(listViewItem.getIcon());
        nameTextView.setText(listViewItem.getNameStr());
        speciesTextView.setText(listViewItem.getSpeciesStr());
        genderTextView.setText(listViewItem.getGenderStr());
        ageTextView2.setText(listViewItem.getAgeStr());

        return convertView;
    }
    public void addItemDog(Drawable icon, String name, String species, String gender, String age) {
        ListViewItem item = new ListViewItem();

        item.setIcon(icon);
        item.setNameStr(name);
        item.setSpeciesStr(species);
        item.setGenderStr(gender);
        item.setAgeStr(age);
        listViewItemList.add(item);
    }
}
