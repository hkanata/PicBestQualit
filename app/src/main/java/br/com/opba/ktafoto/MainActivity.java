package br.com.opba.ktafoto;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import br.com.opba.ktafoto.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private Button tirarFoto;
    private ImageView imageView;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private String currentPhotoPath;
    private SharedPreferences sharedPreferences;

    private String fileName;
    private View vv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("OpbaPref",MODE_PRIVATE);


        setSupportActionBar(binding.toolbar);

        imageView = (ImageView) findViewById(R.id.imageView);

        String pasta = sharedPreferences.getString("pasta", "");



        if(pasta.isEmpty()) {
            showDial();
        }

        tirarFoto = (Button) findViewById(R.id.tirarFoto);
        tirarFoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                vv = view;

                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath()+"/sov3/"+sharedPreferences.getString("pasta", ""));
                dir.mkdirs();
                fileName = "photo" + sharedPreferences.getString("arquivo", "");
                File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try{
                    //File imageFile = File.createTempFile(fileName, ".jpg", dir);
                    File imageFile = new File(dir+"/"+fileName+".jpg");

                    currentPhotoPath = imageFile.getAbsolutePath();

                    Uri imageUri = FileProvider.getUriForFile(MainActivity.this, "br.com.opba.fileprovider", imageFile);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);


                }catch (Exception e){
                    e.printStackTrace();
                }

                /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }*/


            }
        });

        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        //appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


    }

    public void showDial(){
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialoglayout);

        EditText pasta = dialog.findViewById(R.id.etPasta);
        EditText arquivo = dialog.findViewById(R.id.etArquivo);

        String pastaShare = sharedPreferences.getString("pasta", "");
        String arquivoShare = sharedPreferences.getString("arquivo", "1");

        if(!pastaShare.isEmpty()){
            pasta.setText(pastaShare);
        }
        if(!arquivoShare.isEmpty()){
            arquivo.setText(arquivoShare);
        }

        Button btnSalvar = dialog.findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("LOG_OBA", pasta.getText().toString());
                Log.i("LOG_OBA", arquivo.getText().toString());

                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("pasta", pasta.getText().toString());
                myEdit.putString("arquivo", arquivo.getText().toString());
                myEdit.commit();


                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("LOG_OBA", "ae");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            imageView.setImageBitmap(bitmap);

            Integer cont = Integer.valueOf(sharedPreferences.getString("arquivo", ""));
            Log.i("LOG_OBA", ""+(cont+1));

            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            Integer ix = cont+1;
            myEdit.putString("arquivo", ix.toString());
            myEdit.commit();

            Snackbar.make(vv, "Salvo na pasta: sov3/"+sharedPreferences.getString("pasta", "")+"/"+fileName+".jpg", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();


        }
        /*if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);


            BitmapDrawable draw = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = draw.getBitmap();


            FileOutputStream outStream = null;
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath()+"/SaveImages");
            dir.mkdirs();
            String fileName = String.format("%d.jpg", System.currentTimeMillis());
            File outFile = new File(dir, fileName);


            try{
                outStream = new FileOutputStream(outFile);
            }catch (Exception e){
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            try{
                outStream.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                outStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showDial();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}