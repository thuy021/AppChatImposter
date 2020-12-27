package com.example.appchat.Retrofit2;

import android.widget.BaseAdapter;

import com.example.appchat.Models.BanBe;
import com.example.appchat.Models.ConversationMap;
import com.example.appchat.Models.DataMessage;
import com.example.appchat.Models.GoiY;
import com.example.appchat.Models.Message;
import com.example.appchat.Models.MessageNhom;
import com.example.appchat.Models.NguoiDung;
import com.example.appchat.Models.Room;
import com.example.appchat.Models.ThanhVien;
import com.example.appchat.Models.UpLoadFile;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface DataClient {

    @POST("/")
    Call<Message> ThemNguoiDung(@Body NguoiDung nguoiDung);

    @POST("/dangnhap")
    Call<Message> DangNhap(@Body NguoiDung nguoiDung);

    @GET("/sdt={sdt}")
    Call<Message> GetThongTinNguoiDung_bySDT(@Path("sdt") String sdt, @HeaderMap Map<String, String> token);

    @GET("/id={id}")
    Call<Message> GetThongTinNguoiDung_byID(@Path("id") int id, @HeaderMap Map<String, String> token);

    @PATCH("/")
    Call<Message> SuaThongTin(@Body NguoiDung nguoiDung, @HeaderMap Map<String, String> token);

    @POST("/checksdt")
    Call<Message> CheckSoDienThoai(@Body NguoiDung nguoiDung);

    @PATCH("/updatepass")
    Call<Message> UpdatePassword(@Body NguoiDung nguoiDung);

    @POST("/getTrangThaiFriend")
    Call<Message> CheckTrangThaiBanBe(@Body BanBe banBe);

    @POST("/sendRequestAddFriend")
    Call<Message> SendRequestAddFriend(@Body BanBe banBe);

    @POST("/deleterequest")
    Call<Message> DeleteRequestFriend(@Body BanBe banBe);

    @POST("/acceptrequest")
    Call<Message> AcceptRequestFriend(@Body BanBe banBe);

    @POST("/getListFriend")
    Call<Message> GetListFriend(@Body BanBe banBe);

    @POST("/getlistrequestfriend")
    Call<Message> GetListRequestFriend(@Body BanBe banBe);

    @POST("/getListSendRequestFriend")
    Call<Message> GetListSendRequestFriend(@Body BanBe banBe);

    @POST("/addRoom")
    Call<Message> AddRoomChat(@Body Room room);

    @POST("/deleteRoom")
    Call<Message> DeleteRoomChat(@Body Room room);

    @POST("/findIdRoom")
    Call<Message> FindRoomChat(@Body Room room);

    @POST("/createTable")
    Call<Message> CreateTable(@Body Room room);

    @POST("/scanFirstItemMessage")
    Call<DataMessage> ScanFirstItemMessage(@Body Room room);

    @POST("/scanItemMessage")
    Call<DataMessage> ScanItemMessage(@Body Room room);

    @GET("/getAllRoom/id={id}")
    Call<ArrayList<ConversationMap>> GetAllConversationFor_A_User(@Path("id") int id);

    @Multipart
    @POST("/upLoadS3")
    Call<UpLoadFile> UpLoadFile(@Part MultipartBody.Part image);

    @POST("/createGroup")
    Call<MessageNhom> CreateGroup (@Body ThanhVien thanhVien);

    @POST("/addItemGroup")
    Call<MessageNhom> AddItemGroup(@Body ThanhVien thanhVien);

    @GET("/getListGroup={id}")
    Call<MessageNhom> GetListGroup(@Path("id") int id);

    @GET("/checkTruongNhom={id}")
    Call<MessageNhom> CheckTruongNhom(@Path("id") String id);

    @GET("/getListThanhVien={id}")
    Call<MessageNhom> GetListThanhVien(@Path("id") String id);

    @POST("/getListGoiY")
    Call<Message> GetListGoiY(@Body GoiY goiY);
}
