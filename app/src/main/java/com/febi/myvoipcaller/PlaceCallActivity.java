package com.febi.myvoipcaller;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.calling.Call;

import java.util.ArrayList;
import java.util.List;

import static com.febi.myvoipcaller.R.id.stopButton;

public class PlaceCallActivity extends BaseActivity {
    private ArrayList<ContactInfoStructure> mContactInfoStructureArrayList;

    private Button mCallButton;
    private EditText mCallName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mCallName           = (EditText) findViewById(R.id.callName);
        mCallButton         = (Button) findViewById(R.id.callButton);
        Button stopButton   = (Button) findViewById(R.id.stopButton);
        Button showButton   = (Button) findViewById(R.id.id_show_button);

        mCallButton.setEnabled(false);
        mCallButton.setOnClickListener(buttonClickListener);
        stopButton.setOnClickListener(buttonClickListener);
        showButton.setOnClickListener(editClickListener);
    }

    @Override
    protected void onServiceConnected() {
        TextView userName   = (TextView) findViewById(R.id.loggedInName);
        userName.setText(getSinchServiceInterface().getUserName());
        mCallButton.setEnabled(true);
    }

    private boolean isPermissionsGranted() {
        if (ContextCompat.checkSelfPermission(PlaceCallActivity.this,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(PlaceCallActivity.this,
                    Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(PlaceCallActivity.this,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    ActivityCompat.requestPermissions(PlaceCallActivity.this,
                            new String[]{Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.RECORD_AUDIO}, 30);
                }
            } else {
                ActivityCompat.requestPermissions(PlaceCallActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS,
                                Manifest.permission.READ_PHONE_STATE}, 20);
            }
        } else {
            ActivityCompat.requestPermissions(PlaceCallActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 10);
        }
        return false;
    }

    OnClickListener editClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(isPermissionsGranted()) {
                if(mContactInfoStructureArrayList != null && mContactInfoStructureArrayList.size() > 0)
                    showContactsList();
                else
                    populateSpinnerWithContacts();
            }
        }
    };

    private void populateSpinnerWithContacts() {
        mContactInfoStructureArrayList                  = new ArrayList<>();
        String[] fromColumns    = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone._ID};
        Cursor cursor           = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                fromColumns,
                null,
                null,
                null);

        if(cursor != null) {
            cursor.moveToFirst();
            do {
                ContactInfoStructure contactInfoStructure   = new ContactInfoStructure();
                contactInfoStructure.setContactName(cursor.getString(0));
                contactInfoStructure.setPhoneNumber(cursor.getString(1));
                contactInfoStructure.setContactId(cursor.getInt(2));
                mContactInfoStructureArrayList.add(contactInfoStructure);

            }while(cursor.moveToNext());

            cursor.close();
        }

        if(mContactInfoStructureArrayList != null && mContactInfoStructureArrayList.size() > 0)
            showContactsList();
    }

    private void showContactsList(){
        ArrayList<String> arrayList = new ArrayList<>();
        for(ContactInfoStructure contactInfoStructure : mContactInfoStructureArrayList) {
            arrayList.add(contactInfoStructure.getContactName());
        }
        ContactDialog contactDialog = new ContactDialog(PlaceCallActivity.this);
        contactDialog.show();
    }

    private void stopButtonClicked() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        finish();
    }

    private void callButtonClicked() {
        String phoneNumber  = mCallName.getText().toString();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a number to call", Toast.LENGTH_LONG).show();
            return;
        }

        Call call           = getSinchServiceInterface().callPhoneNumber(phoneNumber);
        String callId       = call.getCallId();

        Intent callScreen   = new Intent(this, CallScreenActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        startActivity(callScreen);
    }

    private OnClickListener buttonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isPermissionsGranted()) {
                switch (v.getId()) {
                    case R.id.callButton:
                        callButtonClicked();
                        break;

                    case stopButton:
                        stopButtonClicked();
                        break;
                }
            }
        }
    };

    private class ContactDialog extends Dialog implements OnClickListener {
        private ListView mListView;
        private EditText mSearchText                = null;
        private ContactsAdapter mContactsAdapter    = null;

        public ContactDialog(Context context) {
            super(context);
            setContentView(R.layout.dialog_layout);
            this.setTitle("Select Contact");

            mSearchText     = (EditText) findViewById(R.id.id_search_area);
            mListView       = (ListView) findViewById(R.id.id_dialog_list);

            mSearchText.addTextChangedListener(filterTextWatcher);

            mContactsAdapter    = new ContactsAdapter(context, R.layout.list_item_layout, mContactInfoStructureArrayList);
            mListView.setAdapter(mContactsAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    dismiss();
                    if(mContactsAdapter.getItem(position) != null) {
                        String phoneNo  = mContactsAdapter.getItem(position).getPhoneNumber();
                        mCallName.setText(phoneNo);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
        }

        private TextWatcher filterTextWatcher = new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mContactsAdapter.getFilter().filter(s);
            }
        };

        @Override
        public void onStop() {
            mSearchText.removeTextChangedListener(filterTextWatcher);
        }
    }

    private class ContactsAdapter extends ArrayAdapter<ContactInfoStructure> {
        private List<ContactInfoStructure>originalData  = null;
        private List<ContactInfoStructure>filteredData  = null;
        private ItemFilter mFilter = new ItemFilter();
        private Context mContext;

        public ContactsAdapter(Context context, int resource, List<ContactInfoStructure> objects) {
            super(context, resource, objects);
            mContext            = context;
            this.filteredData   = objects ;
            this.originalData   = objects ;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row                = convertView;
            ContactHolder holder;

            if(row == null) {
                LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
                row                 = inflater.inflate(R.layout.list_item_layout, parent, false);

                holder              = new ContactHolder();
                holder.txtTitle     = (TextView)row.findViewById(R.id.id_list_textView);

                row.setTag(holder);
            } else {
                holder              = (ContactHolder) row.getTag();
            }

            ContactInfoStructure contact = filteredData.get(position);
            holder.txtTitle.setText(contact.getContactName());

            return row;
        }

        public int getCount() {
            return filteredData.size();
        }

        public ContactInfoStructure getItem(int position) {
            return filteredData.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        private class ContactHolder {
            TextView txtTitle;
        }

        public Filter getFilter() {
            return mFilter;
        }

        private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterableString ;
                String filterString     = constraint.toString().toLowerCase();
                FilterResults results   = new FilterResults();

                final List<ContactInfoStructure> originalList   = originalData;
                int count               =   originalList.size();
                final ArrayList<ContactInfoStructure> newList                 = new ArrayList<>(count);

                for (int i = 0; i < count; i++) {
                    filterableString    = originalList.get(i).getContactName();
                    if (filterableString.toLowerCase().contains(filterString)) {
                        newList.add(originalList.get(i));
                    }
                }

                results.values  = newList;
                results.count   = newList.size();

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData    = (ArrayList<ContactInfoStructure>) results.values;
                notifyDataSetChanged();
            }
        }
    }
}
