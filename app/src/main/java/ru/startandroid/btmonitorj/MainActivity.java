package ru.startandroid.btmonitorj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import ru.startandroid.btmonitorj.adapter.BtConsts;

public class MainActivity extends AppCompatActivity {
    private MenuItem menuItem; // для доступа к картинке блютуза в любом месте
    private BluetoothAdapter btAdapter; // чип блютуза на телефоне
    private final int ENABLE_REQUEST = 15;
    private SharedPreferences mainPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menuItem = menu.findItem(R.id.id_bt_button); // icon доступна на уровне класса
        setBtIcon();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // нажатие на кнопку блютуз
        if (item.getItemId() == R.id.id_bt_button) {
            // если блютуз не включен запрашиваем разрешение
            if (!btAdapter.isEnabled()) {
                enableBt();
            } else {
                // 1-2 секунды идет отключение: напрямую меняем картинку в меню
                btAdapter.disable();
                menuItem.setIcon(R.drawable.ic_bt_enable);
            }
        }

        if (item.getItemId() == R.id.id_menu) {

            // переход на активити устройств при условии что блютуз включен
            if (btAdapter.isEnabled()) {
                Intent i = new Intent(MainActivity.this, BtListActivity.class);
                startActivity(i);   
            } else {
                Toast.makeText(this, "Включите блютуз для перехода на экран со списком сопряженных модулей", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    // обработка ответа на запрс разрешения на включение блютуз
    // по коду: если пришел ответ на наш запрос (вкл блютуз?) то придет число 15
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ENABLE_REQUEST) {
            // получено разрешение - меняем картинку
            if (resultCode == RESULT_OK) {
              setBtIcon();
            }
        }
    }

    private void setBtIcon() {
        if (btAdapter.isEnabled()) {
            menuItem.setIcon(R.drawable.ic_bt_disable);
        } else {
            menuItem.setIcon(R.drawable.ic_bt_enable);
        }
    }

    private void init() {
        btAdapter = BluetoothAdapter.getDefaultAdapter(); // доступ к адаптеру
        mainPref = getSharedPreferences(BtConsts.MY_PREF, Context.MODE_PRIVATE);
        Log.d("MyLog", "Bt mac : " + mainPref.getString(BtConsts.MAC_KEY, "no bt selected"));
    }

    // включение блютуза с помощью интента
    private void enableBt() {

        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(i, ENABLE_REQUEST);
    }
}