package cn.amtxts.testroom;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private UserDao userDao;
    private EditText etFirst;
    private EditText etLast;
    private ListView lvContent;
    private TestAdapter mAdapter;
    private List<User> userList;
    private User mUser;
    private CompositeDisposable disposable;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        disposable = new CompositeDisposable();
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").build();
        dbHelper = new AppDbHelper(db);
        initView();
    }

    private void initView() {
        userList = new ArrayList<>();
        etFirst = findViewById(R.id.etFirst);
        etLast = findViewById(R.id.etLast);
        lvContent = findViewById(R.id.lvContent);
        mAdapter = new TestAdapter();
        lvContent.setAdapter(mAdapter);
        lvContent.setOnItemClickListener(this);
        lvContent.setOnItemLongClickListener(this);
    }

    public void add(View view) {
        User user = new User();
        user.firstName = etFirst.getText().toString();
        user.lastName = etLast.getText().toString();
        disposable.add(dbHelper.insertUser(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {

                    }
                }));
    }

    public void del(View view) {
        if (mUser == null) return;
        disposable.add(dbHelper.delUser(mUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
        mUser = null;
    }

    public void update(View view) {
        if (mUser == null) return;
        mUser.firstName = etFirst.getText().toString();
        mUser.lastName = etLast.getText().toString();
        disposable.add(dbHelper.updateUser(mUser)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe());
        mUser = null;
    }

    public void all(View view) {
        disposable.add(dbHelper.getAllUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(@NonNull List<User> users) throws Exception {
                        userList = users;
                        mAdapter.notifyDataSetChanged();
                    }
                }));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mUser = userList.get(i);
        etFirst.setText(userList.get(i).firstName);
        etLast.setText(userList.get(i).lastName);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        return false;
    }

    class TestAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userList.size();
        }

        @Override
        public Object getItem(int i) {
            return userList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View inflate = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_test, null);
            TextView tvFirst = inflate.findViewById(R.id.tvFirst);
            TextView tvLast = inflate.findViewById(R.id.tvLast);
            tvFirst.setText(userList.get(i).firstName);
            tvLast.setText(userList.get(i).lastName);
            return inflate;
        }
    }

}
