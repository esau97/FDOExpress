package com.example.fdoexpress.uiTrabajador.home;

import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.*;
import android.util.Log;
import android.util.SparseArray;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.fdoexpress.PeticionListener;
import com.example.fdoexpress.R;
import com.example.fdoexpress.Tasks.MainAsyncTask;
import com.example.fdoexpress.Tasks.UpdateLocationTask;
import com.example.fdoexpress.Utils.Codigos;
import com.example.fdoexpress.Utils.Constantes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

public class TrabajadorHomeFragment extends Fragment {
    private SurfaceView cameraView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private Spinner spinner;
    private String token;
    private SharedPreferences preferences;
    private Vibrator vibrator;
    private View root;
    private EditText editText;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_trabajador_home, container, false);
        spinner = root.findViewById(R.id.spinnerEstado);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        editText = root.findViewById(R.id.edit_descripcion);
        String lista [] = {"1.- Pendiente", "2.- En tránsito", "3.- En instalaciones", "4.- En reparto", "5.- Entregado", "6.- Ausente","7.- Asociar vehículo"};
        ArrayAdapter<String> valoresSpinner = new ArrayAdapter<String>(root.getContext(),
                android.R.layout.simple_spinner_item, lista);

        valoresSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setSelection(3);
        spinner.setAdapter(valoresSpinner);
        cameraView=root.findViewById(R.id.camera_view);
        barcodeDetector =
                new BarcodeDetector.Builder(root.getContext().getApplicationContext())
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();
        cameraSource =
                new CameraSource.Builder(root.getContext().getApplicationContext(),
                        barcodeDetector).setRequestedPreviewSize(1600, 1024)
                        .setAutoFocusEnabled(true)
                        .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // verifico si el usuario dio los permisos para la camara
                if (ActivityCompat.checkSelfPermission(TrabajadorHomeFragment.this.getContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // verificamos la version de Android que sea al menos la M para mostrar
                        // el dialog de la solicitud de la camara
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.CAMERA)) ;
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                7);
                    }
                    return;
                } else {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException ie) {
                        Log.e("CAMERA SOURCE", ie.getMessage());
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {

                    // obtenemos el token
                    token = barcodes.valueAt(0).displayValue.toString();
                    // verificamos que el token anterior no se igual al actual
                    // esto es util para evitar multiples llamadas empleando el mismo token
                    Log.i("token", token);
                    // Código 11 + Datos del pedido

                    String text = spinner.getSelectedItem().toString();

                    preferences = getActivity().getSharedPreferences(Constantes.STRING_PREFERENCES,MODE_PRIVATE);
                    String enviar="";
                    if(!text.substring(0,1).equals("7")){
                        token+="&"+editText.getText().toString();
                        enviar = "13&"+text.substring(0,1)+"&"+token+"&"+preferences.getString(Constantes.USER_CODE,"");
                    }else{
                        enviar = "17&"+token+"&"+preferences.getString(Constantes.USER_CODE,"");
                    }


                    MainAsyncTask log = new MainAsyncTask(new PeticionListener() {
                        @Override
                        public void callback(String accion) {
                            tratarMensaje(accion);
                        }
                    },enviar);
                    log.execute();
                    vibrator.hasVibrator();
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void tratarMensaje(String codigo){
        String respuesta = "";
        String argumentos[] = codigo.split("&");
        System.out.println("Recibido"+codigo);
        int num = Integer.parseInt(argumentos[0]);
        switch (Codigos.codigo_cliente(num)) {
            case PEDIDO_ANADIDO:
                Snackbar.make(root, "Se ha cambiado correctamente el estado del pedido.", Snackbar.LENGTH_LONG)
                        .show();
                break;
            case ERROR:
                mostrarError(argumentos[1]);
                break;
        }
    }
    public void mostrarError(String codigo){
        int cod = Integer.parseInt(codigo);
        switch (cod){
            case 1:
                Snackbar.make(root, "Se ha producido un error al cambiar el estado del pedido.", Snackbar.LENGTH_LONG)
                        .show();
                break;
        }
    }

    public void pedirPermiso (final String permiso, final int REQUEST_CODE) {

        /*
        * if (ContextCompat.checkSelfPermission(getActivity(),
            Manifest.permission.RECORD_AUDIO) + ContextCompat
            .checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE))
            != PackageManager.PERMISSION_GRANTED) {
        * */
        if (shouldShowRequestPermissionRationale(
                Manifest.permission.CAMERA)) ;
        requestPermissions(new String[]{Manifest.permission.CAMERA},
                7);
        if (ContextCompat.checkSelfPermission(getActivity(), permiso) + ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "You have already granted this permission!", Toast.LENGTH_SHORT).show();
            /*UpdateLocationTask updateLocationTask = new UpdateLocationTask();
            updateLocationTask.execute();*/

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(getActivity().getApplicationContext())
                        .setTitle("Se necesita permiso")
                        .setMessage("Es requerido para el correcto funcionamiento de la aplicación")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{permiso}, REQUEST_CODE);
                            }
                        })
                        .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{permiso}, REQUEST_CODE);
                Toast.makeText(getActivity(), "You should granted this permission!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
