package home.myapplication.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import home.myapplication.R;
import home.myapplication.appL.AppConfig;
import home.myapplication.controller.CameraPreview;
import home.myapplication.controller.HttpHandler;
import home.myapplication.controller.HttpResponseHandler;
import home.myapplication.model.Query;
import home.myapplication.model.User;
import home.myapplication.service.LocationService;

// import net.sourceforge.zbar.android.CameraTest.CameraPreview;
/* Import ZBar Class files */

/**
 * Esta clase contiene el control de la interface de la camara que captura codigos de barras
 */
public class CameraActivity extends Activity implements HttpResponseHandler {
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private Context context;

    TextView scanText;
    ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    private User user;
    private String barcodeString;
    private Location location;
//    Las siguientes lineas fueron agregadas para poder implementar el lector de codigo de barras.
    static {
        System.loadLibrary("iconv");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        se obtiene el usuario logueado
        Bundle b = getIntent().getExtras();
        user = b.getParcelable("user");
//      se inicia el servicio de localizacion
        LocationService service = new LocationService(getApplicationContext());
        location = service.getLocation();

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        scanText = (TextView) findViewById(R.id.scan_text);
    }

    /**
     * Reescanea de ser neseracio
     * @param view
     */
    public void reScan(View view){
        if (barcodeScanned) {
            resumeCamera();
        }
    }

    /**
     * Este metodo permite volver al contexto de la camara, es decir borra el producto escaneado
     *
     * */
    public void resumeCamera(){
        barcodeScanned = false;
        scanText.setText(context.getString(R.string.title_scanning));
        mCamera.setPreviewCallback(previewCb);
        mCamera.startPreview();
        previewing = true;
        mCamera.autoFocus(autoFocusCB);
    }

    /**
     * Retorna el codigo de barras del producto escaneado
     * @return a barcode
     */
    private String getResult() {
        return barcodeString;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCameraInstance();

        /* Instance barcode scanner */
            scanner = new ImageScanner();
            scanner.setConfig(0, Config.X_DENSITY, 3);
            scanner.setConfig(0, Config.Y_DENSITY, 3);
            if (mCamera != null) {
                mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
                FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
                preview.addView(mPreview);
            }
        }
        resumeCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    /**
     * A safe way to sendRequest an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
        }
        return c;
    }

    /**
     * Libera la camara, generalmente usado cuando se pasa de activity
     */
    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
            mPreview.getHolder().removeCallback(mPreview);
        }
    }


    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Parameters parameters = camera.getParameters();
            Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                barcodeString = scanner.getResults().iterator().next().getData();
                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    scanText.setText(context.getString(R.string.title_barcode) + sym.getData());
                    barcodeScanned = true;
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 3000);
        }
    };

    /**
     * Se ocupa de realizar una consulta, con la ubicacion actual para recibir los comercios mas
     * cercanos al usuario
     * @param view
     */
    public void sendRequest(View view) {
        HttpHandler http = new HttpHandler(AppConfig.URL_COMMERCES_SUG,HttpHandler.GET_REQUEST);
        if(location == null){
            Toast notificacion = Toast.makeText(context, context.getString(R.string.msg_services_disable), Toast.LENGTH_SHORT);
            notificacion.show();
            finish();
        }
        Toast notificacion = Toast.makeText(context, ""+location.getProvider(), Toast.LENGTH_SHORT);
        notificacion.show();
        Log.e("CAmeraActivity","provider: "+location.getProvider());

        http.addParams("latitud", String.valueOf(location.getLatitude()));
        http.addParams("longitud", String.valueOf(location.getLongitude()));
        http.addParams("usuario","'"+user.getMail()+"'");
        http.setListener(this);
        http.sendRequest();
    }

    /**
     * En caso de que la consulta sea exitosa, se procesa la info recibida.
     * @param data informacion recibida
     */
    @Override
    public void onSuccess(Object data) {
        if (!barcodeScanned) {// si no se escanearon productos.
            Toast notificacion = Toast.makeText(context, context.getString(R.string.msg_scan_a_product), Toast.LENGTH_SHORT);
            notificacion.show();
        } else {// si se escaneo algun producto se pasa al siguiente activity
            Intent j = new Intent(context, CommerceListViewActivity.class);// la siguiente activity permite seleccionar el comercio
            //donde se compro el producto
            Query query = new Query();// se crea la consulta y se inicializa el codigo de barras.
            query.setBarcode(getResult());
            // se inicializa la localizacion del usuario
            query.setLocation(location);
            // se comparte con el siguiente activity el usuario, la consulta y la informacion recibida (en este caso
            // una lista de los comercios mas cercanos al usuario)
            j.putExtra("user",user);
            j.putExtra("query", query);
            j.putExtra("data", data.toString());
            // se inicia el siguiente activity.
            context.startActivity(j);
        }
    }
 }
