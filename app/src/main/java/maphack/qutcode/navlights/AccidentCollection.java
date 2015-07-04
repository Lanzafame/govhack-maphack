package maphack.qutcode.navlights;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import maphack.qutcode.navlights.filters.Filters;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

/**
 * Created by kane on 4/07/2015.
 */
public class AccidentCollection {
    private ArrayList<Accident> accidents;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private GoogleMap mMap;
    private String CSV_PATH = "csv/weightedlocations.csv";


    public AccidentCollection(GoogleMap map, Context context) {
        mMap = map;
        setupHeatMap();
        try {
            setupData(context);
        }
        catch (Exception e) {

        }
    }

    public void updateHeatMap() {
        List<WeightedLatLng> list = new ArrayList<WeightedLatLng>();
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        int renderCount = 0;
        for (Accident a : accidents) {
            if (underRenderLimit(renderCount)) {
                if (bounds.contains(a.getLocation()) && Filters.toDisplay(a)) {
                    Log.i("Accident Added", "" + a);
                    list.add(a);
                    renderCount++;
                }
            } else {
                break;
            }
        }

        if (list.size() != 0) {
            mProvider.setWeightedData(list);
            mOverlay.clearTileCache();
        }
    }

    private boolean underRenderLimit(int renderCount) {
        return renderCount < 350;
    }

//    private void setupData(Context context) {
//        accidents = new ArrayList<>();
//        DatabaseHelp DataBase = new DatabaseHelp(context);
//        Cursor c = DataBase.getAccidents();
//        c.moveToFirst();
//        while(!c.isAfterLast()) {
//            LatLng location = new LatLng(c.getDouble(1), c.getDouble(2));
//            accidents.add(new Accident(location, c.getInt(3), c.getInt(4), c.getInt(5), c.getInt(6)));
//            c.moveToNext();
//        }
//    }

    private void setupData(Context context) throws Exception {
        accidents = new ArrayList<>();
        List<String[]> rows = readCsv(context);

        for (String[] r : rows) {
            LatLng loc = new LatLng(parseDouble(r[0]), parseDouble(r[1]));
            accidents.add(new Accident(loc, parseDouble(r[2])));
        }
    }

    public final List<String[]> readCsv(Context context) {
        List<String[]> accidentsList = new ArrayList<String[]>();
        AssetManager assetManager = context.getAssets();

        try {
            InputStream csvStream = assetManager.open(CSV_PATH);
            InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
            CSVReader csvReader = new CSVReader(csvStreamReader);
            String[] line;

            // throw away the header
            // I didn't include a header
//            csvReader.readNext();

            while ((line = csvReader.readNext()) != null) {
                accidentsList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accidentsList;
    }


    private void setupHeatMap() {
        ArrayList<WeightedLatLng> temp = new ArrayList<>();
        temp.add(new WeightedLatLng(new LatLng(1, 1), 1));

        mProvider = new HeatmapTileProvider.Builder()
                .weightedData(temp)
                .radius(20)
                .opacity(0.8)
                .build();
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }
}
