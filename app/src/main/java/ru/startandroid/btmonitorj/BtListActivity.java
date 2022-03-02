package ru.startandroid.btmonitorj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ru.startandroid.btmonitorj.adapter.BtAdapter;
import ru.startandroid.btmonitorj.adapter.ListItem;

public class BtListActivity extends AppCompatActivity {
private ListView listView;
private BtAdapter adapter;
    private BluetoothAdapter btAdapter; // чип блютуза на телефоне
    private List<ListItem> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_list);
        init();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        list = new ArrayList<>();

        // панель наверху: устанавливаем стрелку возврата
        ActionBar ab = getSupportActionBar();
        if (ab == null) return;
        ab.setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.id_lv);
        //List<ListItem> list = new ArrayList<>();
//        ListItem item = new ListItem();
//        item.setBtName("BT-12340");
//        list.add(item);
//        list.add(item);
//        list.add(item);
//        list.add(item);
//        list.add(item);
        adapter = new BtAdapter(this, R.layout.bt_list_item, list);
        listView.setAdapter(adapter);

        // !!! только при включенном блютуз переходим на BtListActivity
        getPairedDevices();
    }

    private void getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        list.clear();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                ListItem item = new ListItem();
                item.setBtName(device.getName());
                item.setBtMac(device.getAddress());
                list.add(item);
            }
            // перезагрузка адаптера в связи с изменениями в списке
            adapter.notifyDataSetChanged();
        }
    }
}