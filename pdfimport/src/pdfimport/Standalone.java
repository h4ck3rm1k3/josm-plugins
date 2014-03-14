package pdfimport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.io.OsmExporter;
import pdfimport.pdfbox.PdfBoxParser;
import java.awt.Color;

public class Standalone
{ 

    private File fileName;
    private PathOptimizer data;
    private OsmDataLayer layer;
    protected OsmDataLayer newLayer;
    FilePlacement placement; // = new FilePlacement();

    void parsePlacement(
                        double minX,
                        double minY,
                        double maxX,
                        double maxY,
                        
                        double minEastField,
                        double minNorthField,
                        double maxEastField,
                        double maxNorthField
                        
                        ) {
        
        //placement.projection = selectedProjection.getProjection();
        
        this.placement.setPdfBounds(
                                    minX,
                                    minY,
                                    maxX,
                                    maxY);
        
        this.placement.setEastNorthBounds(
                                          minEastField,
                                          minNorthField,
                                          maxEastField,
                                          maxNorthField);
        //return placement;
    }

    PathOptimizer loadPDF(File fileName) throws Exception
    {
        
        double nodesTolerance = 0.0;
        double small_objects_tolerance = 0;
        double large_objects_tolerance = 0;
        Color color = null;
        int maxPaths = Integer.MAX_VALUE;
        boolean splitOnShapeClosed = true;
        boolean splitOnSingleSegment = true;
        boolean splitOnOrthogonal = true;
        
        // if (this.mergeCloseNodesCheck.isSelected()) {
        ///nodesTolerance = Double.parseDouble(this.mergeCloseNodesTolerance.getText());       
        // color filter
        //        String colString = this.colorFilterColor.getText().replace("#", "");
        //color = new Color(Integer.parseInt(colString, 16));
        
        // if (this.limitPathCountCheck.isSelected()) {
        //maxPaths = 1000;

        boolean splitOnColorChangeCheck = true;

        PathOptimizer data = new PathOptimizer(nodesTolerance, color, splitOnColorChangeCheck);
        
        PdfBoxParser parser = new PdfBoxParser(data);

        parser.parse(fileName, maxPaths);
                
        //if (this.removeParallelSegmentsCheck.isSelected()) {
        //double tolerance = Double.parseDouble(this.removeParallelSegmentsTolerance.getText());
        //data.removeParallelLines(tolerance);
        
        if (nodesTolerance > 0.0) {
            data.mergeNodes();
        }

        data.mergeSegments();   
        if (small_objects_tolerance > 0) {
            data.removeSmallObjects(small_objects_tolerance);
        }

        if (large_objects_tolerance > 0) {
            data.removeLargeObjects(large_objects_tolerance);
        }

        data.splitLayersByPathKind(
                                   splitOnShapeClosed,
                                   splitOnSingleSegment, 
                                   splitOnOrthogonal);
        data.finish();
        return data;
    }

    public void process (
                         File newFileName,
                         String layer_name,
                         File outfile                         
                         ) throws Exception 
    {
        this.data = this.loadPDF(newFileName);
        OsmBuilder.Mode mode = OsmBuilder.Mode.Debug;
        OsmExporter exporter = new OsmExporter();
        OsmBuilder builder = new OsmBuilder(placement);
        DataSet data = builder.build(this.data.getLayers(), OsmBuilder.Mode.Final);
        OsmDataLayer result = new OsmDataLayer(data, layer_name, null);
        result.onPostLoadFromFile();       
        
        fileName = newFileName;
        newLayer = null;
        //FilePlacement placement =  loadPlacement();
        //LoadPdfDialog.this.setPlacement(placement);
        //private void setPlacement(FilePlacement placement) {    
        exporter.exportData(outfile, layer);

    }

    public static void main(String [] args)
	{
            System.out.printf("Hello World\n");

            Properties p = new Properties();
            //p.load(new FileInputStream(propertiesFile));
            //pl.fromProperties(p);


	}

}
