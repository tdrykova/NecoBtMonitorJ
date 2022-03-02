package ru.startandroid.btmonitorj.adapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.startandroid.btmonitorj.R;

// класс который заполняет список ListView
public class BtAdapter extends ArrayAdapter<ListItem> {
    public static final String DEF_ITEM_TYPE = "normal"; // из списка смартфона
    public static final String TITLE_ITEM_TYPE = "title"; // строка списка Найденные устр-ва
    public static final String DISCOVERY_ITEM_TYPE = "discovery"; // найденное устр-во
    // копия списка из конструктора для его использования в getView
    private List<ListItem> mainList; // 1ый список типа ListItem
    private List<ViewHolder> viewHolderList; // 2ой список типа ViewHolder для работы с чекбоксами

    private SharedPreferences pref;
    // для хранения данных о выбранном устройстве в памяти

    // конструктор: передает контекст который передаем с активити
    // где будет запускаться адаптер (запускается в BtListActivity
    // который подключаем к ListView)
    // + передаем список устройств сопряженных со смартфоном
    public BtAdapter(@NonNull Context context, int resource, List<ListItem> btList) {
        super(context, resource, btList);
        mainList = btList; // теперь btList доступен на уровне класса а не только в конструкторе
        viewHolderList = new ArrayList<>();
        pref = context.getSharedPreferences(BtConsts.MY_PREF, Context.MODE_PRIVATE);
        // название таблицы и уровень доступа (доступ к таблице только в данном приложении)
    }

    // view эл-та списка, создаем свой view
    // запускается столько раз, сколько эл-тов с списке
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        switch (mainList.get(position).getItemType()) {
            case TITLE_ITEM_TYPE:
                convertView = titleItem(convertView, parent);
                break;
            default:
                convertView = defaultItem(convertView, position, parent);
                break;
        }


        return convertView;
    }


    // передаем позицию выбранного устройства из списка mainList
    private void savePref(int position) {
        SharedPreferences.Editor editor = pref.edit(); // открытие таблицы для записи
        editor.putString(BtConsts.MAC_KEY, mainList.get(position).getBtDevice().getAddress());
        editor.apply();
    }

    // ViewHolder - сохраняет отрисованные эл-ты списка при скрытии во время скрола
    static class ViewHolder {

        TextView tvBtName;
        CheckBox chBtSelected;

    }

    private View defaultItem(View convertView, int position, ViewGroup parent) {
        ViewHolder viewHolder;

        // эл-т в ListView (в списке) создается первый раз
        if (convertView == null) {
            viewHolder = new ViewHolder();
            // разметка
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bt_list_item, null, false);
            // установка имени и состояния бокса во viewHolder
            viewHolder.tvBtName = convertView.findViewById(R.id.tvBtName);
            viewHolder.chBtSelected = convertView.findViewById(R.id.checkBox);
            // сохранение имени и состояния бокса во viewHolder
            convertView.setTag(viewHolder);

            viewHolderList.add(viewHolder);

        } else {
            // convertView == null то есть применили скролл
            // эл-т уже создан, есть viewHolder для него: есть данные разметки
            viewHolder = (ViewHolder) convertView.getTag(); // тк convertView.setTag(viewHolder);

        }

        viewHolder.tvBtName.setText(mainList.get(position).getBtDevice().getName());
        viewHolder.chBtSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (ViewHolder holder : viewHolderList) {
                    holder.chBtSelected.setChecked(false); // все очистили
                }
                viewHolder.chBtSelected.setChecked(true); // одно устройство выбрали
                savePref(position);
            }
        });
        // мак сохраненный? отмечаем
        if (pref.getString(BtConsts.MAC_KEY, "no bt selected").equals(mainList.get(position).getBtDevice().getAddress())) {
            viewHolder.chBtSelected.setChecked(true);
        }
        // mainList.get(position) = ListItem с мак адресом и имененм
        // viewHolder.chBtSelected.setChecked(true);

        // return super.getView(position, convertView, parent);
    return convertView;
    }

    private View titleItem(View convertView, ViewGroup parent) {

        // эл-т в ListView (в списке) создается первый раз
        if (convertView == null) {
            // разметка
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bt_list_item_title, null, false);
        }
        return convertView;
    }
}
