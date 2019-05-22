package com.lx.checkameterclient.view;

import java.io.File;

import com.lx.checkameterclient.MainActivity;
import com.lx.checkameterclient.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Video;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;


public class FileShow extends Activity {

	private static final int MENU_DELETE = Menu.FIRST;
	private static final int MENU_RENAME = Menu.FIRST + 1;
	private File[] files;
	private String[] names;
	private String[] paths;
	private GridView fileGrid;
	private BaseAdapter adapter = null;
	private String path;
	private EditText etRename;
	private File file;
	private int parentId=3;
	private int flag;
	private TextView txt_files;

	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.map_file_show);
		path = getIntent().getStringExtra("path");
		//flag=getIntent().getIntExtra("flag", 0);
		txt_files=(TextView)findViewById(R.id.txt_filenum);
		
		File file = new File(path);
		if (!file.exists()) {///////////////
			file.mkdirs();
		}/////////////////
		files = file.listFiles();
		txt_files.setText(String.valueOf(files.length));
		fileGrid = (GridView) findViewById(R.id.arc_hf_file_show);
		adapter = new fileAdapter(this);
		fileGrid.setAdapter(adapter);
		showFileItems();
		fileGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				File f = new File(paths[arg2]);
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				
				intent.setAction(android.content.Intent.ACTION_VIEW);
//				intent.setAction(android.content.Intent.ACTION_GET_CONTENT);
				String type = thisFileType(names[arg2]);
				intent.setDataAndType(Uri.fromFile(f), type);
				startActivity(intent);
			}
		});

		// 注册上下文菜单
		registerForContextMenu(fileGrid);
	}

	/*
	 * 覆写上下文菜单
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = null;

		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			return;
		}
		menu.setHeaderTitle(names[info.position]);
		menu.add(0, MENU_DELETE, 1, "删除");
		menu.add(0, MENU_RENAME, 2, "重命名");
	}

	/*
	 * 上下文菜单监听
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		File file = new File(paths[info.position]);
		switch (item.getItemId()) {
		case MENU_DELETE:
			file.delete();
			showFileItems();
			return true;
		case MENU_RENAME:
			fileRename(file);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	/*
	 * 获取文件
	 */
	private void showFileItems() {
		File file = new File(path);
		files = file.listFiles();
		int count = files.length;
		txt_files.setText(String.valueOf(files.length));
		if(count<=0){
			Toast.makeText(this,"没有符合条件的文件",Toast.LENGTH_SHORT).show();
		}else{
			names = new String[count];
			paths = new String[count];
			for (int i = 0; i < count; i++) {
				File f = files[i];
				names[i] = f.getName();
				paths[i] = f.getPath();
			}
		}
		adapter.notifyDataSetChanged();
	}

	/*
	 * 获取文件类型
	 */
	public static String thisFileType(String name) {
		String type = "";
		String end = name.substring(name.lastIndexOf(".") + 1, name.length())
				.toLowerCase();
		if (end.equals("jpg")) {
			type = "image";
		} else if (end.equals("mp4")) {
			type = "video";
		} else {
			type = "*";
		}
		type += "/*";
		return type;
	}

	/**
	 * 重命名
	 */
	private void fileRename(File file) {
		this.file = file;
		View view = getLayoutInflater().inflate(R.layout.map_file_rename, null);
		etRename = (EditText) view.findViewById(R.id.arc_hf_file_rename);
		new AlertDialog.Builder(this).setView(view)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String newName = etRename.getText().toString().trim();
						File newFile = new File(path, newName);
						if (newFile.exists()) {
							showMsg(newName + "已经存在，请重新输入");
						} else
							FileShow.this.file.renameTo(newFile);
						showFileItems();
					}
				}).setNegativeButton("取消", null).show();
	}

	/*
	 * 消息提示
	 */
	private Toast toast;

	public void showMsg(String arg) {
		if (toast == null) {
			toast = Toast.makeText(this, arg, Toast.LENGTH_SHORT);
		} else {
			toast.cancel();
			toast.setText(arg);
		}
		toast.show();
	}
	 /**
	  * 根据指定的图像路径和大小来获取缩略图
	  * 此方法有两点好处：
	  *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	  *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
	  *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
	  *        用这个工具生成的图像不会被拉伸。
	  * @param imagePath 图像的路径
	  * @param width 指定输出图像的宽度
	  * @param height 指定输出图像的高度
	  * @return 生成的缩略图
	  */
	 public Bitmap getImageThumbnail(String imagePath, int width, int height) {
	  Bitmap bitmap = null;
	  BitmapFactory.Options options = new BitmapFactory.Options();
	  options.inJustDecodeBounds = true;
	  // 获取这个图片的宽和高，注意此处的bitmap为null
	  bitmap = BitmapFactory.decodeFile(imagePath, options);
	  // 设为 false
	  // 计算缩放比
	  int h = options.outHeight;
	  int w = options.outWidth;
	  int beWidth = w / width;
	  int beHeight = h / height;
	  int be = 1;
	  if (beWidth < beHeight) {
	   be = beWidth;
	  } else {
	   be = beHeight;
	  }
	  if (be <= 0) {
	   be = 1;
	  }
	  options.inSampleSize = be;
	  // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
	  options.inJustDecodeBounds = false; 
	  bitmap = BitmapFactory.decodeFile(imagePath, options);
	  // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
	  bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
	    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
	  return bitmap;
	 }
	 
	 /**
	  * 获取视频的缩略图
	  * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
	  * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
	  * @param videoPath 视频的路径
	  * @param width 指定输出视频缩略图的宽度
	  * @param height 指定输出视频缩略图的高度度
	  * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
	  *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
	  * @return 指定大小的视频缩略图
	  */
	 private Bitmap getVideoThumbnail(String videoPath, int width, int height,
	   int kind) {
	  Bitmap bitmap = null;
	  // 获取视频的缩略图
	  bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
	  System.out.println("w"+bitmap.getWidth());
	  System.out.println("h"+bitmap.getHeight());
	  bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
	    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
	  return bitmap;
	 }
	 
	/*
	 * File adapter设置
	 */
	class fileAdapter extends BaseAdapter {
		Context context;

		public fileAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return files.length;
		}

		@Override
		public Object getItem(int arg0) {
			// return files[arg0];
			return names[arg0];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String type = thisFileType(names[position]);
			convertView = getLayoutInflater().inflate(R.layout.map_file_item,
					null);
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.arc_hf_file_icon);
			TextView name = (TextView) convertView
					.findViewById(R.id.arc_hf_file_name);
			if (type.equals("video/*")) {
				Bitmap videoIcon = ThumbnailUtils.createVideoThumbnail(
						paths[position], Video.Thumbnails.MINI_KIND);
				videoIcon=getVideoThumbnail(paths[position],250,220,Video.Thumbnails.MINI_KIND);
				icon.setImageBitmap(videoIcon);
			} else if (type.equals("image/*")) {
//				Bitmap bitmap = BitmapFactory.decodeFile(paths[position]);
//				Bitmap imgIcon = ThumbnailUtils.extractThumbnail(bitmap, 150,
//						120);
				Bitmap imgIcon=getImageThumbnail(paths[position],250,220);
				icon.setImageBitmap(imgIcon);
			}
			name.setText(names[position]);
			return convertView;
		}
	}
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
           //这里写你要在用户按下返回键同时执行的动作
                //moveTaskToBack(true);            //核心代码：屏蔽返回行为
        	if(flag==0){
    		Intent intent = new Intent();
//    		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		intent.setClass(this, MainActivity.class);
//    		intent.setClassName(this,"com.mycamera.MCamera");
    		intent.putExtra("parentId", parentId);
    		//hjh//startActivity(intent);
        	}
    		this.finish();
        	
                
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


 
}