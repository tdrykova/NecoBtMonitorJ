package ru.startandroid.btmonitorj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ru.startandroid.btmonitorj.adapter.BtAdapter;
import ru.startandroid.btmonitorj.adapter.ListItem;

public class BtListActivity extends AppCompatActivity {
    private final int BT_REQUEST_PERM = 111;
    private ListView listView;
    private BtAdapter adapter;
    private BluetoothAdapter btAdapter; // чип блютуза на телефоне
    private List<ListItem> list;
    private boolean isBtPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_list);
        //getBtPermission();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter f1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter f2 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(bReceiver, f1);
        registerReceiver(bReceiver, f2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(bReceiver); // остановка приема результатов
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bt_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.id_search) {
            if (getBtPermission()) {
                ListItem itemTitle = new ListItem();
                itemTitle.setItemType(BtAdapter.TITLE_ITEM_TYPE);
                list.add(itemTitle);
                //list - список из эл-тов ListItem поэтому преобразуем надпись в этот тип
                // обновление адаптера с новым списком
                adapter.notifyDataSetChanged();

                btAdapter.startDiscovery(); // поиск устройств
            }

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
        adapter = new BtAdapter(this, R.layout.bt_list_item, list);
        listView.setAdapter(adapter);

        // !!! только при включенном блютуз переходим на BtListActivity
        getPairedDevices();

        onItemClickListener();
    }

    // нажатие на эл-т списка Дисковери
    private void onItemClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                ListItem item = (ListItem) adapterView.getItemAtPosition(position);
                if (item.getItemType().equals(BtAdapter.DISCOVERY_ITEM_TYPE)) {
                    item.getBtDevice().createBond(); // запрос на сопряжение
                   // item.setItemType(BtAdapter.DEF_ITEM_TYPE);
                }
                //item.setItemType(BtAdapter.DEF_ITEM_TYPE);
            }
        });
    }

    private void getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        list.clear();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                ListItem item = new ListItem();
                item.setBtDevice(device); // передаем всю инф об устройстве
//                item.setBtName(device.getName());
//                item.setBtMac(device.getAddress());
                list.add(item);
            }
            // перезагрузка адаптера в связи с изменениями в списке
            adapter.notifyDataSetChanged();
        }
    }

    // проверка состояния ответа
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == BT_REQUEST_PERM) {
            // если запрс из массива запросов 
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isBtPermissionGranted = true;
                Toast.makeText(this, "Разрешение получено", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Нет разрешения на поиск блютуз устройств", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean getBtPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // диалог запроса разрешения
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, BT_REQUEST_PERM);
        } else {
            isBtPermissionGranted = true; // есть разрешение
        }
        return isBtPermissionGranted;
    }

    // BroadcastReceiver - приемник который принимает интенты (сообщения от или к системе)
    private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // проверка интент сообщения от системы
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
               // device.createBond(); // запрос подключения
                ListItem item = new ListItem();
                item.setBtDevice(device);
                item.setItemType(BtAdapter.DISCOVERY_ITEM_TYPE);
                list.add(item);
                adapter.notifyDataSetChanged();
                //Toast.makeText(context, "Founded device : " + device.getName(), Toast.LENGTH_SHORT).show();
            }


        }
    };
}