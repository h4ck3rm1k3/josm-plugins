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
import java.io.PrintStream;

public class Standalone
{ 


    // void parsePlacement(
    //                     double minX,
    //                     double minY,
    //                     double maxX,
    //                     double maxY,                       
    //                     double minEastField,
    //                     double minNorthField,
    //                     double maxEastField,
    //                     double maxNorthField                       
    //                     ) {        
    //     //placement.projection = selectedProjection.getProjection();        
    //     this.placement.setPdfBounds(
    //                                 minX,
    //                                 minY,
    //                                 maxX,
    //                                 maxY);        
    //     this.placement.setEastNorthBounds(
    //                                       minEastField,
    //                                       minNorthField,
    //                                       maxEastField,
    //                                       maxNorthField);
    //     //return placement;
    // }

    PathOptimizer data;

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


        this.data = new PathOptimizer(nodesTolerance, color, splitOnColorChangeCheck);
        
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
                         File placement_file,
                         File outfile                         
                         ) throws Exception 
    {
        String layer_name= new String("Test");
        data = this.loadPDF(newFileName);
        OsmBuilder.Mode mode = OsmBuilder.Mode.Debug;
        OsmExporter exporter = new OsmExporter();
        FilePlacement placement = new FilePlacement();

        // load placement from file
        Properties p = new Properties();
        p.load(new FileInputStream(placement_file));
        placement.fromProperties(p);
        OsmBuilder builder = new OsmBuilder(placement);

        DataSet data = builder.build(this.data.getLayers(), OsmBuilder.Mode.Final);
        OsmDataLayer result = new OsmDataLayer(data, layer_name, null);
        result.onPostLoadFromFile();       
        exporter.exportData(outfile, result);

    }

    public static void main(String [] args)
	{
            System.out.printf("Hello World\n");

            // command line parameter
            if(args.length != 3) {
                System.err.println("Invalid command line, exactly three argument required");
                System.exit(1);
            }
            File infile = new File(args[0]);
            File placement = new File(args[1]);
            File outfile = new File(args[2]);

            System.out.printf("infile %s \n", infile);
            Standalone obj =  new Standalone();
            try {
                obj.process(infile, placement, outfile);
            } catch (Exception e) {
                System.out.printf("exception %s \n", e);
                 e.printStackTrace(new PrintStream(System.out));
            }

	}

}
