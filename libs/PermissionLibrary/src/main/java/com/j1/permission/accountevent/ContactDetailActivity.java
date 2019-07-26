package com.j1.permission.accountevent;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.j1.permission.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenjing.liu on 19/6/17 in J1.
 * 用户在勾选联系人的时候，进入的联系人的详细信息，需要确认选中的号码，然后返回给进行确认权限的Activity中
 *
 * @author wenjing.liu
 */
public class ContactDetailActivity extends Activity {
    private ImageView ivBack;
    private TextView tvContactName;
    private ListView lvContactNumbers;
    private TextView tvNoPhoneNumberShow;

    private String contactName = "";
    private List<Number> numbers;
    private NumberDetailAdapter contactsAdapter;
    private ContentResolver contentResolver;
    /**
     * 标记上一个选中的电话号码的position,用来在选中当前的时候将之前的集合中的元素置为false
     */
    private int isPreSelectedPosition = 0;
    /**
     * 标记当前选中的电话号码的position,用来在选中当前位置返回给前一个页面
     */
    private int isSelectedPosition = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact_detail);
        initGlobalVariables();
        initWidgets();
        loadContactDetail();
    }


    private void initGlobalVariables() {
        numbers = new ArrayList<>();
        contentResolver = getContentResolver();
    }

    private void initWidgets() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvContactName = (TextView) findViewById(R.id.tv_contact_name);
        lvContactNumbers = (ListView) findViewById(R.id.lv_contact_number);
        tvNoPhoneNumberShow = (TextView) findViewById(R.id.tv_no_contact_show);
        lvContactNumbers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleOnItemClick(position);
            }
        });
        contactsAdapter = new NumberDetailAdapter(ContactDetailActivity.this, numbers);
        lvContactNumbers.setAdapter(contactsAdapter);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * 根据data传过来的信息查询contact id
     *
     * @return
     */
    private String queryContactId() {
        Uri uri = getIntent().getData();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        cursor.close();
        return id;

    }

    /**
     * 从数据库中读取该联系人的详细信息
     */
    private void loadContactDetail() {
        String contactId = queryContactId();
        Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                String.format("%s=%s", ContactsContract.CommonDataKinds.Phone.CONTACT_ID, contactId), null, null);

        numbers.clear();
        while (phones.moveToNext()) {
            String type = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            String name = getString(ContactsContract.CommonDataKinds.Phone.getTypeLabelResource(
                    Integer.parseInt(type)));
            String num = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            //赋值
            Number number = new Number();
            number.setName(name);
            number.setNumber(num);
            if (numbers.size() == 0) {
                //仅第一个赋值为true
                number.setIsSelected(true);
            }
            numbers.add(number);
        }
        phones.close();
        refreshLayout();
    }

    private void refreshLayout() {
        tvContactName.setText(contactName);
        tvNoPhoneNumberShow.setVisibility(numbers.size() == 0 ? View.VISIBLE : View.GONE);
        lvContactNumbers.setVisibility(numbers.size() == 0 ? View.GONE : View.VISIBLE);
        contactsAdapter.notifyDataSetChanged();
    }


    /**
     * 处理onItemClick事件
     *
     * @param position
     */
    private void handleOnItemClick(int position) {

        //更新UI的选中状态
        if (isPreSelectedPosition >= 0 && isPreSelectedPosition < numbers.size()) {
            numbers.get(isPreSelectedPosition).setIsSelected(false);
            numbers.get(position).setIsSelected(true);
            contactsAdapter.notifyDataSetChanged();
            isPreSelectedPosition = position;
        }
        isSelectedPosition = position;
        finish();
    }

    @Override
    public void finish() {
        if (numbers.isEmpty() || isSelectedPosition < 0 || isSelectedPosition >= numbers.size()) {
            Intent intent = new Intent();
            intent.putExtra(ContactEvent.CONTACT_NAME, contactName);
            intent.putExtra(ContactEvent.CONTACT_PHONE, "");
            setResult(RESULT_OK, intent);
            super.finish();
            return;
        }

        Number number = numbers.get(isSelectedPosition);
        Intent intent = new Intent();
        intent.putExtra(ContactEvent.CONTACT_NAME, contactName);
        intent.putExtra(ContactEvent.CONTACT_PHONE, number.getNumber());
        setResult(RESULT_OK, intent);
        super.finish();
    }
}
