package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.Adapter.ItemClickListener;
import com.example.appchat.Adapter.NhanTinAdapter;
import com.example.appchat.Models.DataMessage;
import com.example.appchat.Models.ItemMessage;
import com.example.appchat.Models.Message;
import com.example.appchat.Models.NhanTin;
import com.example.appchat.Models.Room;
import com.example.appchat.Models.UpLoadFile;
import com.example.appchat.Retrofit2.APIUtils;
import com.example.appchat.Retrofit2.DataClient;
import com.example.appchat.Socket.SocketChat;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NhanTinDonActivity extends AppCompatActivity {

    SocketChat client_socket;
    ImageButton btnGui_Chat, btnTuyChon, btnBack_DanhBa, btnGuiTinNhan_File, btnGuiTinNhan_Hinh;
    EditText edtNhanTin;
    TextView tvTenNguoiNhan, tvNguoiNhanTyping;
    RecyclerView recycleTinNhan;
    ProgressBar prgbr_isTyping;

    ConstraintLayout container_isTyping;

    ArrayList<NhanTin> listNhanTin;
    NhanTinAdapter nhanTinAdapter;
    SharedPreferences preferences;
    String ten;
    String id_room = "";
    int id_user_2 = 0;
    int id_user_1 = 0;
    String url = "";
    String tenFile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhan_tin_don);
        client_socket = new SocketChat();

        //Ẩn Thanh Trạng Thái
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init();
        Back_DanhBa();
        CheckRoom();
        SendMessage();
        TuyChon();
        UploadImage();
        UpLoadFile();
        DoaloadFile();
        //Lắng Nghe Người Dùng Nhập Dữ Liệu
        edtNhanTin_TextChanged();

        ListenSocketFromServer();
    }

    private void edtNhanTin_TextChanged() {
        edtNhanTin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtNhanTin.length() == 0) {
                    btnGui_Chat.setVisibility(View.GONE);
                    btnGuiTinNhan_Hinh.setVisibility(View.VISIBLE);
                    btnGuiTinNhan_File.setVisibility(View.VISIBLE);
                } else {
                    btnGui_Chat.setVisibility(View.VISIBLE);
                    btnGuiTinNhan_Hinh.setVisibility(View.GONE);
                    btnGuiTinNhan_File.setVisibility(View.GONE);
                }

                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("id_room", id_room);
                    jsonObject.put("id_user_01", id_user_1);
                    jsonObject.put("id_user_02", id_user_2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                client_socket.getmClient().emit("CLIENT_TYPING", jsonObject);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    protected void Init() {
        ten = getIntent().getStringExtra("ten");
        preferences = getSharedPreferences("data_dang_nhap", MODE_PRIVATE);
        listNhanTin = new ArrayList<>();
        recycleTinNhan = findViewById(R.id.recycleTinNhanChat);
        LinearLayoutManager layoutManager = new LinearLayoutManager(NhanTinDonActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setStackFromEnd(true);
        nhanTinAdapter = new NhanTinAdapter(NhanTinDonActivity.this, listNhanTin);
        recycleTinNhan.setAdapter(nhanTinAdapter);
        recycleTinNhan.setLayoutManager(layoutManager);
        edtNhanTin = (EditText) findViewById(R.id.edtTinNhan_Don);
        btnGui_Chat = (ImageButton) findViewById(R.id.btn_Gui_Tin_Nhan_Don);
        btnGuiTinNhan_Hinh = (ImageButton) findViewById(R.id.btn_Gui_Hinh_Don);
        btnGuiTinNhan_File = (ImageButton) findViewById(R.id.btn_Gui_Link_Don);
        btnTuyChon = findViewById(R.id.btn_Tuy_Chon_Chat_Don);
        btnBack_DanhBa = findViewById(R.id.btnBack_Fragment_Tro_Chuyen);

        tvTenNguoiNhan = findViewById(R.id.tvTenNguoiNhan);
        tvTenNguoiNhan.setText(ten);

        tvNguoiNhanTyping = findViewById(R.id.tvNguoiNhanDangGo);
        tvNguoiNhanTyping.setText(ten + " đang nhập ");

        prgbr_isTyping = findViewById(R.id.prgbr_isTyping);
        Sprite threeBounce = new ThreeBounce();
        prgbr_isTyping.setIndeterminateDrawable(threeBounce);

        container_isTyping = findViewById(R.id.container_isTyping);
    }

    private void UpLoadFile() {
        btnGuiTinNhan_File.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(NhanTinDonActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NhanTinDonActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("*/*");
                startActivityForResult(intent, 2);
            }
        });
    }

    private void DoaloadFile(){
        nhanTinAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                url = "";
                url = listNhanTin.get(position).getTinNhan();
                if (url.equals("")){
                    url = listNhanTin.get(position).getTinGui();
                }
                tenFile = listNhanTin.get(position).getUrlFile();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, 1000);
                    }
                    else {
                        StartDowloading();
                    }
                }
                else {
                    StartDowloading();
                }
            }
        });
    }

    private void StartDowloading(){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(tenFile);
        request.setDescription("Đang tải xuống ....");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, tenFile);
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    StartDowloading();
                }
                else {
                    Toast.makeText(NhanTinDonActivity.this, "Quyền truy cập bị từ chối", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void UploadImage() {
        btnGuiTinNhan_Hinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(NhanTinDonActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NhanTinDonActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            List<Bitmap> bitmaps = new ArrayList<>();
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    try {
                        InputStream is = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        bitmaps.add(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Uri imageUri = data.getData();
                try {
                    InputStream is = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    bitmaps.add(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (Bitmap bitmap : bitmaps) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    UpImage(bitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            ArrayList<Uri> listUri = new ArrayList<>();
            if (null != data) { // checking empty selection
                if (null != data.getClipData()) { // checking multiple selection or not
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        listUri.add(uri);
                    }
                } else {
                    Uri uri = data.getData();
                    listUri.add(uri);
                }
            }
            for (Uri uri : listUri) {
                try {
                    InputStream is = getContentResolver().openInputStream(uri);
                    String type = getContentResolver().getType(uri);
                    Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
                    returnCursor.moveToFirst();
                    String namFile = returnCursor.getString(returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    File file = new File(getCacheDir(), namFile);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        writeStreamToFile(is, file);
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        UpFile(file, type);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void UpFile(File file, String type) throws IOException {
        MultipartBody.Part body = buildFileBodyPart(file, type);
        DataClient client = APIUtils.getData();
        Call<UpLoadFile> call = client.UpLoadFile(body);
        call.enqueue(new Callback<UpLoadFile>() {
            @Override
            public void onResponse(Call<UpLoadFile> call, Response<UpLoadFile> response) {
                if (response.isSuccessful()) {
                    UpLoadFile up = response.body();
                    if (up.getSuccess() == 1) {
                        listNhanTin.add(new NhanTin(up.getMessage(), "", "file", false, file.getName(), ten));
                        String linkS3 = up.getLocationArray().get(0);
                        JSONObject object = new JSONObject();
                        int userSend = id_user_1;
                        int userReceive = id_user_2;
                        try {
                            object.put("userSend", userSend);
                            object.put("userReceive", userReceive);
                            object.put("message", linkS3);
                            object.put("fileName", file.getName());
                            object.put("type_message", "file");
                            object.put("tableName", id_room);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        client_socket.getmClient().emit("CLIENT_GUI_TIN_NHAN", object);
                        nhanTinAdapter.notifyDataSetChanged();
                        recycleTinNhan.scrollToPosition(listNhanTin.size() - 1);
                        edtNhanTin.setText("");
                    }
                }
            }

            @Override
            public void onFailure(Call<UpLoadFile> call, Throwable t) {

            }
        });
    }

    private MultipartBody.Part buildFileBodyPart(File file, String type) throws IOException {
        RequestBody reqFile = RequestBody.create(MediaType.parse(type), file);
        return MultipartBody.Part.createFormData(file.toString(), file.getName(), reqFile);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void writeStreamToFile(InputStream input, File file) throws IOException {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try (OutputStream output = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
                    int read;
                    while ((read = input.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }
                    output.flush();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getBase64String(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        return base64String;
    }

    private void UpImage(Bitmap bitmap) throws IOException {
        MultipartBody.Part body = buildImageBodyPart("photos", bitmap);
        DataClient client = APIUtils.getData();
        Call<UpLoadFile> call = client.UpLoadFile(body);
        call.enqueue(new Callback<UpLoadFile>() {
            @Override
            public void onResponse(Call<UpLoadFile> call, Response<UpLoadFile> response) {
                if (response.isSuccessful()) {
                    UpLoadFile up = response.body();
                    if (up.getSuccess() == 1) {
                        String base64 = getBase64String(bitmap);
                        listNhanTin.add(new NhanTin(base64, "", "image", true, "", ten));
                        String linkS3 = up.getLocationArray().get(0);
                        JSONObject object = new JSONObject();
                        int userSend = id_user_1;
                        int userReceive = id_user_2;
                        try {
                            object.put("userSend", userSend);
                            object.put("userReceive", userReceive);
                            object.put("message", linkS3);
                            object.put("type_message", "image");
                            object.put("fileName", "");
                            object.put("tableName", id_room);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        client_socket.getmClient().emit("CLIENT_GUI_TIN_NHAN", object);
                        nhanTinAdapter.notifyDataSetChanged();
                        recycleTinNhan.scrollToPosition(listNhanTin.size() - 1);
                        edtNhanTin.setText("");
                    }
                }
            }

            @Override
            public void onFailure(Call<UpLoadFile> call, Throwable t) {

            }
        });
    }

    private MultipartBody.Part buildImageBodyPart(String fileName, Bitmap bitmap) throws IOException {
        File leftImageFile = convertBitmapToFile(fileName, bitmap);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), leftImageFile);
        return MultipartBody.Part.createFormData(fileName, leftImageFile.getName(), reqFile);
    }

    private File convertBitmapToFile(String fileName, Bitmap bitmap) throws IOException {
        //create a file to write bitmap data
        File file = new File(getCacheDir(), fileName + ".jpeg");
        file.createNewFile();

        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return file;
    }

    //Lắng Nghe Các Event Trả Về Từ Server
    private void ListenSocketFromServer() {
        client_socket.getmClient().on("SERVER_GUI_TIN_NHAN", NhanTinNhanServer);
        client_socket.getmClient().on("SERVER_IS_TYPING", SenderIsTyping);
    }

    private void CheckRoom() {
        id_user_2 = getIntent().getIntExtra("id_user", 0);
        id_user_1 = preferences.getInt("MaNguoiDung", 0);
        Room room = new Room();
        room.setId_user_1(id_user_1);
        room.setId_user_2(id_user_2);
        DataClient client = APIUtils.getData();
        Call<Message> call = client.FindRoomChat(room);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        id_room = response.body().getId_room();
                    }
                    JSONObject object = new JSONObject();
                    try {
                        object.put("TenPhong", id_room);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LoadTinNhan();
                    client_socket.getmClient().emit("DANG_KY_PHONG", object);
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {

            }
        });
    }

    protected void SendMessage() {
        //Send text
        btnGui_Chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listNhanTin.add(new NhanTin(edtNhanTin.getText().toString().trim(), "", "text", false, "", ten));
                JSONObject object = new JSONObject();
                int userSend = id_user_1;
                int userReceive = id_user_2;
                try {
                    object.put("userSend", userSend);
                    object.put("userReceive", userReceive);
                    object.put("message", edtNhanTin.getText().toString().trim());
                    object.put("type_message", "text");
                    object.put("tableName", id_room);
                    object.put("fileName", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                client_socket.getmClient().emit("CLIENT_GUI_TIN_NHAN", object);
                nhanTinAdapter.notifyDataSetChanged();
                recycleTinNhan.scrollToPosition(listNhanTin.size() - 1);
                edtNhanTin.setText("");
            }
        });
    }

    protected void Back_DanhBa() {
        btnBack_DanhBa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void TuyChon() {
        btnTuyChon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NhanTinDonActivity.this, TuyChonChatActivity.class);
                intent.putExtra("Sender", ten);
                startActivity(intent);
            }
        });
    }

    //Lắng Nghe Tin Nhắn Từ Sever
    private Emitter.Listener NhanTinNhanServer = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    String tinNhan_Gui = "";
                    String type_message = "";
                    String fileName = "";
                    try {
                        tinNhan_Gui = object.getString("message");
                        type_message = object.getString("type_message");
                        fileName = object.getString("fileName");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (type_message.equals(("text"))) {
                        listNhanTin.add(new NhanTin("", tinNhan_Gui, "text", false, "", ten));
                    } else if (type_message.equals(("file"))) {
                        listNhanTin.add(new NhanTin("", tinNhan_Gui, "file", false, fileName, ten));
                    } else {
                        listNhanTin.add(new NhanTin("", tinNhan_Gui, "image", false, "", ten));
                    }
                    nhanTinAdapter.notifyDataSetChanged();
                    recycleTinNhan.scrollToPosition(listNhanTin.size() - 1);
                    container_isTyping.setVisibility(View.GONE);
                }
            });
        }
    };

    private void LoadTinNhan() {
        Room room = new Room();
        room.setId_room(id_room);
        DataClient client = APIUtils.getData();
        Call<DataMessage> call = client.ScanFirstItemMessage(room);
        call.enqueue(new Callback<DataMessage>() {
            @Override
            public void onResponse(Call<DataMessage> call, Response<DataMessage> response) {
                if (response.isSuccessful()) {
                    ArrayList<ItemMessage> data = response.body().getItems();
                    for (ItemMessage item : data) {
                        if (item.getUserSend() == id_user_1) {
                            if (item.getTypeMessage().equals("text")) {
                                listNhanTin.add(new NhanTin(item.getMessage(), "", "text", false, "", ten));
                            } else if (item.getTypeMessage().equals("image"))
                                listNhanTin.add(new NhanTin(item.getMessage(), "", "image", false, "", ten));
                            else
                                listNhanTin.add(new NhanTin(item.getMessage(), "", "file", false, item.getFileName(), ten));
                        }
                        if (item.getUserReceive() == id_user_1) {
                            if (item.getTypeMessage().equals("text")) {
                                listNhanTin.add(new NhanTin("", item.getMessage(), "text", false, "", ten));
                            } else if (item.getTypeMessage().equals("image"))
                                listNhanTin.add(new NhanTin("", item.getMessage(), "image", false, "", ten));
                            else
                                listNhanTin.add(new NhanTin("", item.getMessage(), "file", false, item.getFileName(), ten));
                        }
                    }
                    nhanTinAdapter.notifyDataSetChanged();
                    recycleTinNhan.scrollToPosition(listNhanTin.size() - 1);
                }
            }

            @Override
            public void onFailure(Call<DataMessage> call, Throwable t) {

            }
        });
    }

    //Lắng Nghe Event Typing Sender
    private Emitter.Listener SenderIsTyping = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    container_isTyping.setVisibility(View.VISIBLE);

                    CountDownTimer timer = new CountDownTimer(5000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            container_isTyping.setVisibility(View.GONE);
                        }
                    };
                    timer.start();
                }
            });
        }
    };
}