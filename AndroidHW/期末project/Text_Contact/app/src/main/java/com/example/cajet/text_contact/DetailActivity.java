package com.example.cajet.text_contact;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class DetailActivity extends Activity implements View.OnClickListener {
    LinearLayout detail;
    EditText et_name;
    EditText et_mobilePhone;
    EditText et_birthday;
    EditText et_familyPhone;
    EditText et_position;
    EditText et_company;
    EditText et_address;
    EditText et_zipCode;
    EditText et_otherContact;
    EditText et_email;
    EditText et_remark;

    ImageButton btn_img;
    Button btn_modify;
    Button btn_delete;
    ImageButton btn_mes;
    ImageButton btn_call;
    ImageButton btn_call_family;
    Button btn_save;
    Button btn_return;

    boolean add = true;

    private HashMap map = null;
    private Boolean flag = false;
    User user = null;

    final static int SET_ICON = 1;
    final static int FILE_SELECT_CODE = 1;
    final static int FILE_SELECT_RESULT = 2;
    final static int RESULT_REQUEST_CODE = 3;
    final static int FILE_SELECT_FAIL = 4;
    TextView filePath;
    ImageView scan;
    String path = "";
    String newPath = "";
    Bitmap bitmap = null;
    Bitmap newBitmap = null;
    private int iconPosition;
    private int newIconPosition;

    ArrayList<Bitmap> iconList = new ArrayList<>();
    ArrayList<String> localImagePath = new ArrayList<>();
    Handler handler;
    Handler handler_icon_select;
    HandlerThread thread;

    public int[] smallIcon =
            new int[] {R.drawable.icon1
                    ,R.drawable.image1,R.drawable.image2,R.drawable.image3
                    ,R.drawable.image4,R.drawable.image5,R.drawable.image6
                    ,R.drawable.image7,R.drawable.image8,R.drawable.image9
                    ,R.drawable.image10,R.drawable.image11,R.drawable.image12
                    ,R.drawable.image13,R.drawable.image14,R.drawable.image15
                    ,R.drawable.image16,R.drawable.image17,R.drawable.image18
                    ,R.drawable.image19,R.drawable.image20,R.drawable.image21
                    ,R.drawable.image22,R.drawable.image23,R.drawable.image24
                    ,R.drawable.image25,R.drawable.image26,R.drawable.image27
                    ,R.drawable.image28,R.drawable.image29,R.drawable.image30 };
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("userMap")) {
            add = false;
            map = (HashMap) intent.getSerializableExtra("userMap");
        }

        setContentView(R.layout.detail);
        SysApplication.getInstance().addActivity(this);

        init();
        if (map != null) {  // modify
            displayData();
            btn_save.setVisibility(View.GONE);
            btn_modify.setOnClickListener(this);
            btn_delete.setOnClickListener(this);
            btn_call.setOnClickListener(this);
            btn_mes.setOnClickListener(this);
            btn_call_family.setOnClickListener(this);
        } else {  // add
            btn_save.setOnClickListener(this);
            btn_modify.setVisibility(View.GONE);
            btn_delete.setVisibility(View.GONE);
            btn_call.setVisibility(View.GONE);
            btn_mes.setVisibility(View.GONE);
            btn_call_family.setVisibility(View.GONE);
        }
        btn_img.setOnClickListener(this);
        btn_return.setOnClickListener(this);

        handler = new Handler(getMainLooper()) {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case SET_ICON:
                        if (bitmap != null) {
                            btn_img.setImageBitmap(bitmap);
                            iconList.add(bitmap);
                            if (!path.equals("") && !localImagePath.contains(path)) {
                                try {
                                    String folderPath = Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/helper/header/";
                                    Log.i("mk dir", folderPath);
                                    File folder = new File(folderPath);
                                    if (folder.exists() && !folder.isDirectory()) {
                                        if (!folder.delete()) {
                                            Log.e("Create Folder Error", "Error occurred when delete exist file.");
                                        }
                                    } else if (!folder.exists()) {
                                        if (!folder.mkdirs()) {
                                            Log.e("Create Folder Error", "Error occurred when create folder.");
                                        }
                                    }
                                    Date curDate = new Date(System.currentTimeMillis());
                                    String str = dateFormat.format(curDate);
                                    path = folderPath + str + ".png";
                                    Log.i("path", path);
                                    File output = new File(path);
                                    if (output.exists()) {
                                        if (!output.delete())
                                            break;
                                    }
                                    if (!output.createNewFile())
                                        break;
                                    FileOutputStream fos = new FileOutputStream(output);
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                    fos.flush();
                                    fos.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        thread = new HandlerThread("ICON_SELECT");
        thread.start();
        handler_icon_select = new Handler(thread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case FILE_SELECT_RESULT:
                        if (scan != null && filePath != null) {
                            scan.setImageBitmap(newBitmap);
                            filePath.setText(newPath);
                        }
                        break;
                    case FILE_SELECT_FAIL:
                        Toast.makeText(DetailActivity.this, "Fail : Something wrong occurred when selecting Icon.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        super.handleMessage(msg);
                        break;
                }
            }
        };
    }
    private void init() {
        for (int icon : smallIcon) {
            iconList.add(BitmapFactory.decodeResource(getResources(), icon));
        }
        File externalDir = Environment.getExternalStorageDirectory();
        String photosPath = externalDir.getAbsolutePath() + "/Android/data/" + getPackageName() + "/helper/header/";
        File folder = new File(photosPath);
        if (folder.exists() && !folder.isDirectory()) {
            if (!folder.delete()) {
                Log.e("Create Folder Error", "Error occurred when delete exist file.");
            }
        } else if (!folder.exists()) {
            if (!folder.mkdirs()) {
                Log.e("Create Folder Error", "Error occurred when create folder.");
            }
        }
        File[] photosFile = (new File(photosPath)).listFiles();
        if (photosFile != null) {
            for (File photoFile : photosFile) {
                localImagePath.add(photoFile.getAbsolutePath());
                iconList.add(BitmapFactory.decodeFile(photoFile.getAbsolutePath()));
            }
        }
        detail = (LinearLayout) findViewById(R.id.detail);
        btn_img = (ImageButton) findViewById(R.id.image_button);
        et_name = (EditText)findViewById(R.id.username);
        et_zipCode = (EditText)findViewById(R.id.zipCode);
        et_birthday = (EditText)findViewById(R.id.birthday);
        et_mobilePhone = (EditText)findViewById(R.id.mobile);
        et_familyPhone = (EditText)findViewById(R.id.family_phone);
        et_address = (EditText)findViewById(R.id.address);
        et_company = (EditText)findViewById(R.id.company);
        et_email = (EditText)findViewById(R.id.email);
        et_otherContact = (EditText)findViewById(R.id.otherContact);
        et_remark = (EditText)findViewById(R.id.remark);
        et_position = (EditText)findViewById(R.id.position);

        // add
        btn_save = (Button)findViewById(R.id.save);
        btn_return = (Button)findViewById(R.id.btn_return);

        // modify
        btn_modify = (Button) findViewById(R.id.btn_modify);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_mes = (ImageButton) findViewById(R.id.btn_mes);
        btn_call = (ImageButton) findViewById(R.id.btn_call);
        btn_call_family = (ImageButton) findViewById(R.id.btn_call_family);

    }
    private void displayData() {
        et_name.setText(String.valueOf(map.get("name")));
        et_zipCode.setText(String.valueOf(map.get("zipCode")));
        et_birthday.setText(String.valueOf(map.get("birthday")));
        et_mobilePhone.setText(String.valueOf(map.get("mobile")));
        et_familyPhone.setText(String.valueOf(map.get("familyPhone")));
        et_address.setText(String.valueOf(map.get("address")));
        et_company.setText(String.valueOf(map.get("company")));
        et_email.setText(String.valueOf(map.get("email")));
        et_otherContact.setText(String.valueOf(map.get("otherContact")));
        et_remark.setText(String.valueOf(map.get("remark")));
        et_position.setText(String.valueOf(map.get("position")));

        int icon = (int)map.get("imageId");
        String iconPath = map.get("imagePath").toString();
        if (iconPath.equals("")) {
            bitmap = BitmapFactory.decodeResource(getResources(), icon);
            btn_img.setImageBitmap(bitmap);
            for (int i = 0; i < smallIcon.length; i++) {
                if (smallIcon[i] == icon)
                    iconPosition = i;
            }
        } else {
            File output = new File(iconPath);
            if (output.exists()) {
                path = iconPath;
                bitmap = BitmapFactory.decodeFile(path);
            } else {
                bitmap = BitmapFactory.decodeResource(getResources(), smallIcon[0]);
            }
            btn_img.setImageBitmap(bitmap);
        }
        setEditTextDisable();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        String name;
        DBHelper helper;
        switch (viewId) {
            case R.id.image_button:
                handler_icon_select.post(new Runnable() {
                    @Override
                    public void run() {
                        initImageChooseDialog();
                    }
                });
                break;
            // add
            case R.id.save:
                //判断姓名是否为空
                name = et_name.getText().toString();
                if(name.trim().equals("")) {
                    Toast.makeText(DetailActivity.this, "姓名不许为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //从表单上获取数据
                user = new User();
                if (iconPosition%iconList.size() >= smallIcon.length) {
                    user.imageId = smallIcon[0];
                } else {
                    user.imageId = smallIcon[iconPosition % iconList.size()];
                }
                user.imagePath = path;
                user.username = name;
                user.zipCode = et_zipCode.getText().toString();
                user.birthday = et_birthday.getText().toString();
                user.mobilePhone = et_mobilePhone.getText().toString();
                user.familyPhone = et_familyPhone.getText().toString();
                user.address = et_address.getText().toString();
                user.company = et_company.getText().toString();
                user.email = et_email.getText().toString();
                user.otherContact = et_otherContact.getText().toString();
                user.remark = et_remark.getText().toString();
                user.position = et_position.getText().toString();

                helper= new DBHelper(DetailActivity.this);
                //也可以借助getInstance()写成DBHelper.getInstance(Add NewActivity.this).insert(user);
                long result = helper.insert(user);
                if (result != -1) {
                    Toast.makeText(DetailActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
                    setResult(1, intent);
                    finish();//才能跳转回MainActivity
                } else {
                    Toast.makeText(DetailActivity.this, "添加失败,请重试", Toast.LENGTH_SHORT).show();
                    setResult(2);
                    finish();
                }
                break;
            case R.id.btn_return:
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                if (map != null && user != null)
                    bundle.putSerializable("user", user);
                intent.putExtras(bundle);
                setResult(1);
                finish();
                break;
            // modify
            case R.id.btn_modify:
                if (!flag) {
                    setEditTextAble();
                    flag = true;
                    btn_modify.setText("保存");
                } else {
                    //判断姓名是否为空
                    name = et_name.getText().toString();
                    if(name.trim().equals("")) {
                        Toast.makeText(DetailActivity.this, "姓名不许为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //从表单上获取数据
                    user = new User();
                    user._id = Integer.valueOf(map.get("_id").toString()); //先把object转string,再把string转int
                    if (iconPosition%iconList.size() >= smallIcon.length) {
                        user.imageId = smallIcon[0];
                    } else {
                        user.imageId = smallIcon[iconPosition % iconList.size()];
                    }
                    user.imagePath = path;
                    user.username = et_name.getText().toString();
                    user.zipCode = et_zipCode.getText().toString();
                    user.birthday = et_birthday.getText().toString();
                    user.mobilePhone = et_mobilePhone.getText().toString();
                    user.familyPhone = et_familyPhone.getText().toString();
                    user.address = et_address.getText().toString();
                    user.company = et_company.getText().toString();
                    user.email = et_email.getText().toString();
                    user.otherContact = et_otherContact.getText().toString();
                    user.remark = et_remark.getText().toString();
                    user.position = et_position.getText().toString();
                    helper = new DBHelper(DetailActivity.this);
                    helper.update(user);
                    setEditTextDisable();
                    flag = false;
                    btn_modify.setText("修改");
                }
                break;
            case R.id.btn_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("(●'◡'●)").setMessage(" 是否删除？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                user = new User();
                                user._id = Integer.valueOf(map.get("_id").toString());
                                DBHelper helper = new DBHelper(DetailActivity.this);
                                helper.delete(user);
                                Intent intent1 = new Intent(DetailActivity.this, MainActivity.class);
                                startActivity(intent1);
                            }
                        }).setNegativeButton("取消", null).show();
                break;
            case R.id.btn_call:
                //调用拨号权限
                if (!et_mobilePhone.getText().toString().equals("")) {
                    Intent intent3 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + et_mobilePhone.getText().toString()));
                    startActivity(intent3);
                }
                break;
            case R.id.btn_mes:
                //调用发短信权限
                if (!et_mobilePhone.getText().toString().equals("")) {
                    Intent intent4 = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms"+"to:"+et_mobilePhone.getText().toString()));
                    startActivity(intent4);
                }
                break;
            case R.id.btn_call_family:
                //调用拨号权限
                if (!et_familyPhone.getText().toString().equals("")) {
                    Intent intent3 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + et_familyPhone.getText().toString()));
                    startActivity(intent3);
                }
                break;
            default:
                break;
        }
    }

    private void setEditTextDisable() {
        btn_img.setEnabled(false);
        et_name.setEnabled(false);
        et_zipCode.setEnabled(false);
        et_birthday.setEnabled(false);
        et_mobilePhone.setEnabled(false);
        et_familyPhone.setEnabled(false);
        et_address.setEnabled(false);
        et_company.setEnabled(false);
        et_email.setEnabled(false);
        et_otherContact.setEnabled(false);
        et_remark.setEnabled(false);
        et_position.setEnabled(false);
    }

    private void setEditTextAble() {
        btn_img.setEnabled(true);
        et_name.setEnabled(true);
        et_zipCode.setEnabled(true);
        et_birthday.setEnabled(true);
        et_mobilePhone.setEnabled(true);
        et_familyPhone.setEnabled(true);
        et_address.setEnabled(true);
        et_company.setEnabled(true);
        et_email.setEnabled(true);
        et_otherContact.setEnabled(true);
        et_remark.setEnabled(true);
        et_position.setEnabled(true);
    }

    private void initImageChooseDialog() {
        newPath = path;
        newBitmap = bitmap;

        AlertDialog.Builder builder=  new AlertDialog.Builder(this);
        builder.setTitle("请选择图像");
        LayoutInflater layoutInflater= LayoutInflater.from(this);
        View view= layoutInflater.inflate(R.layout.dialog_view, detail, false);
        builder.setView(view);

        scan = (ImageView) view.findViewById(R.id.scan);
        filePath = (TextView) view.findViewById(R.id.filePath);

        if (bitmap != null) {
            scan.setImageBitmap(bitmap);
        } else if (iconPosition < smallIcon.length){
            scan.setImageResource(smallIcon[iconPosition]);
        }

        final ImageButton fileSearch = (ImageButton) view.findViewById(R.id.fileSearch);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.horizon);
        LinearLayoutManager layoutManager = new LinearLayoutManager(DetailActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        IconAdapter adapter = new IconAdapter(DetailActivity.this, iconList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new IconAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Bitmap item) {
                newIconPosition = position % iconList.size();
                recyclerView.smoothScrollToPosition(position);
                if (newIconPosition < smallIcon.length) {
                    newPath = "";
                } else {
                    newPath = localImagePath.get(newIconPosition - smallIcon.length);
                }
                newBitmap = iconList.get(newIconPosition);
                scan.setImageBitmap(newBitmap);
                filePath.setText("");
            }
        });
        recyclerView.scrollToPosition(iconPosition);

        fileSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, FILE_SELECT_CODE);
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message message = new Message();
                message.what = SET_ICON;
                path = newPath;
                iconPosition = newIconPosition;
                bitmap = newBitmap;
                handler.sendMessage(message);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Log.i("RESULT_OK", uri.toString() + " " + uri.getScheme());
                    if ("content".equalsIgnoreCase(uri.getScheme())) {
                        String[] projection = { "_data", "_display_name" };
                        Cursor cursor;
                        try {
                            cursor = getContentResolver().query(uri, projection, null, null, null);
                            if (cursor != null) {
                                if (cursor.moveToFirst()) {
                                    newPath = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                                    if (newPath == null) {
                                        newPath = cursor.getString(cursor.getColumnIndexOrThrow("_display_name"));
                                    }
                                    Log.i("path", newPath);
                                    Intent intent = new Intent("com.android.camera.action.CROP");
                                    intent.setDataAndType(uri, "image/*");
                                    // 设置裁剪
                                    intent.putExtra("crop", "true");
                                    // aspectX aspectY 是宽高的比例
                                    intent.putExtra("aspectX", 1);
                                    intent.putExtra("aspectY", 1);
                                    // outputX outputY 是裁剪图片宽高
                                    intent.putExtra("outputX", 200);
                                    intent.putExtra("outputY", 200);
                                    intent.putExtra("return-data", true);
                                    startActivityForResult(intent, RESULT_REQUEST_CODE);
                                }
                                cursor.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case RESULT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data == null) break;
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        newBitmap = extras.getParcelable("data");
                        Message message = new Message();
                        message.what = FILE_SELECT_RESULT;
                        handler_icon_select.sendMessage(message);
                        break;
                    }
                }
                Message message = new Message();
                message.what = FILE_SELECT_FAIL;
                handler_icon_select.sendMessage(message);
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
