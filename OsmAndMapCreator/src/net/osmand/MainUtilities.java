package net.osmand;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.osmand.binary.MapZooms;
import net.osmand.data.diff.AugmentedDiffsInspector;
import net.osmand.data.diff.GenerateDailyObf;
import net.osmand.data.diff.ObfDiffGenerator;
import net.osmand.data.diff.ObfDiffMerger;
import net.osmand.data.diff.ObfRegionSplitter;
import net.osmand.data.index.GenerateRegionTags;
import net.osmand.data.index.IndexUploader;
import net.osmand.data.preparation.IndexCreator;
import net.osmand.data.preparation.OceanTilesCreator;
import net.osmand.impl.ConsoleProgressImplementation;
import net.osmand.osm.MapRenderingTypesEncoder;
import net.osmand.osm.util.ResourceDeleter;
import net.osmand.regions.CountryOcbfGeneration;
import net.osmand.render.RenderingRulesStorage;
import net.osmand.render.RenderingRulesStoragePrinter;
import net.osmand.swing.DataExtractionSettings;
import net.osmand.util.Algorithms;

import org.apache.commons.logging.Log;
import org.xmlpull.v1.XmlPullParserException;

public class MainUtilities {
	private static Log log = PlatformUtil.getLog(MainUtilities.class);


	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			printSynopsys();
		} else if (args[0].equals("--test-osm-live-tag-removal")) {
			generateAllOsmLiveTests(new File(System.getProperty("repo.dir")+"/resources/test-resources/osm_live"),
					System.getProperty("maps.dir"), false);
//			String test = "2017_06_18-10_30_tagRemovalBug_01.xml";
//			String osmLivePath = System.getProperty("repo.dir")+"/resources/test-resources/osm_live/";
//			Algorithms.removeAllFiles(new File(osmLivePath, AugmentedDiffsInspector.DEFAULT_REGION));
//			AugmentedDiffsInspector.main(new String[] { osmLivePath + test, osmLivePath });
//			GenerateDailyObf.main(new String[] { osmLivePath });
		} else {
			String utl = args[0];
			List<String> subArgs = new ArrayList<String>(Arrays.asList(args).subList(1, args.length));
			String[] subArgsArray = subArgs.toArray(new String[args.length - 1]);
			if (utl.equals("check-ocean-tile")) {
				OceanTilesCreator.checkOceanTile(subArgsArray);
			} else if (utl.equals("compare")) {
				BinaryComparator.main(subArgsArray);
			} else if (utl.equals("merge-index")) {
				BinaryMerger.main(subArgsArray);
			} else if (utl.equals("generate-region-tags")) {
				GenerateRegionTags.main(subArgsArray);
			} else if (utl.equals("generate-ocean-tile-osm")) {
				OceanTilesCreator.createJOSMFile(subArgsArray);
			} else if (utl.equals("generate-java-style")) {
				RenderingRulesStoragePrinter.main(subArgsArray);
			} else if (utl.equals("explain-rendering-style")) {
				RenderingRulesStorage.main(subArgsArray);
			} else if (utl.equals("generate-obf-diff")) {
				ObfDiffGenerator.main(subArgsArray);
			} else if (utl.equals("generate-ocean-tile")) {
				OceanTilesCreator.createTilesFile(subArgsArray[0], subArgsArray.length > 1 ? args[1] : null);
			} else if (utl.equals("test-routing")) {
				net.osmand.router.TestRouting.main(subArgsArray);
			} else if (utl.equals("generate-ocbf")) {
				CountryOcbfGeneration.main(subArgsArray);
			} else if (utl.equals("generate-obf")) {
				IndexCreator ic = new IndexCreator(new File("."));
				ic.setIndexMap(true);
				ic.setIndexPOI(true);
				ic.setIndexRouting(true);
				ic.setIndexTransport(true);
				ic.setIndexAddress(true);
				ic.setLastModifiedDate(new File(subArgsArray[0]).lastModified());
				generateObf(subArgsArray, ic);
			} else if (utl.equals("generate-obf-no-address")) {
				IndexCreator ic = new IndexCreator(new File("."));
				ic.setIndexMap(true);
				ic.setIndexPOI(true);
				ic.setIndexRouting(true);
				ic.setIndexTransport(true);
				ic.setLastModifiedDate(new File(subArgsArray[0]).lastModified());
				generateObf(subArgsArray, ic);
			} else if (utl.equals("generate-map")) {
				IndexCreator ic = new IndexCreator(new File("."));
				ic.setIndexMap(true);
				ic.setLastModifiedDate(new File(subArgsArray[0]).lastModified());
				generateObf(subArgsArray, ic);
			} else if (utl.equals("split-obf")) {
				ObfRegionSplitter.main(subArgsArray);
			} else if (utl.equals("merge-bulk-osmlive-day")) {
				ObfDiffMerger.mergeBulkOsmLiveDay(subArgsArray[0]);
			} else if (utl.equals("merge-bulk-osmlive-month")) {
				ObfDiffMerger.mergeBulkOsmLiveMonth(subArgsArray[0]);
			} else if (utl.equals("merge-flat-obf")) {
				ObfDiffMerger.main(subArgsArray);
			} else if (utl.equals("generate-address")) {
				IndexCreator ic = new IndexCreator(new File("."));
				ic.setIndexAddress(true);
				ic.setLastModifiedDate(new File(subArgsArray[0]).lastModified());
				generateObf(subArgsArray, ic);
			} else if (utl.equals("extract-roads-only")) {
				File mainFile = new File(subArgsArray[0]);
				IndexUploader.extractRoadOnlyFile(
						mainFile,
						new File(mainFile.getParentFile(), mainFile.getName().replace(IndexConstants.BINARY_MAP_INDEX_EXT,
								IndexConstants.BINARY_ROAD_MAP_INDEX_EXT)));
			} else if (utl.equals("generate-poi")) {
				IndexCreator ic = new IndexCreator(new File("."));
				ic.setIndexPOI(true);
				ic.setLastModifiedDate(new File(subArgsArray[0]).lastModified());
				generateObf(subArgsArray, ic);

			} else if (utl.equals("delete-unused-strings")) {
				ResourceDeleter.main(subArgsArray);
			} else if (utl.equals("merge-std-files")) {
				BinaryMerger.mergeStandardFiles(subArgsArray);
			} else if (utl.equals("generate-roads")) {
				IndexCreator ic = new IndexCreator(new File("."));
				ic.setIndexRouting(true);
				ic.setLastModifiedDate(new File(subArgsArray[0]).lastModified());
				generateObf(subArgsArray, ic);
			} else if (utl.contentEquals("generate-osmlive-tests")) {
				if (subArgsArray.length < 1) {
					System.out.println("Usage: <path_to_directory_with_resources_project> <optional_path_to_unpack_files>");
					return;
				}
				File testResources = new File(subArgsArray[0]+"/resources/test-resources/osm_live/");
				generateAllOsmLiveTests(testResources, subArgsArray.length > 1 ? subArgsArray[1] : null, false);
			} else if (utl.contentEquals("generate-from-overpass")) {
				if (subArgsArray.length < 3) {
					System.out.println("Usage: PATH_TO_OVERPASS PATH_TO_WORKING_DIR PATH_TO_REGIONS");
					return;
				}
				String[] argsToGenerateOsm = new String[] {
						subArgsArray[0],
						subArgsArray[1],
						subArgsArray[2]
				};
				AugmentedDiffsInspector.main(argsToGenerateOsm);
				String[] argsToGenerateObf = new String[] {
						subArgsArray[1]
				};
				GenerateDailyObf.main(argsToGenerateObf);
			} else {
				printSynopsys();
			}
		}
	}

	private static void generateAllOsmLiveTests(File testResources, String unpackFolder, boolean delete) throws IOException {
		// clean all files
		if (delete) {
			Algorithms.removeAllFiles(new File(testResources, AugmentedDiffsInspector.DEFAULT_REGION));
		}
		for(File f : testResources.listFiles()) {
			if(f.getName().endsWith(".diff.osm")) {
				int DATE_LENGTH = 10;
				String date = f.getName().substring(0, DATE_LENGTH);
				String targetFl = AugmentedDiffsInspector.DEFAULT_REGION + f.getName().substring(DATE_LENGTH) + ".gz";
				FileInputStream fis = new FileInputStream(f);
				File outFl = new File(testResources, AugmentedDiffsInspector.DEFAULT_REGION + "/" + date + "/"
						+ targetFl);
				outFl.getParentFile().mkdirs();
				GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(outFl));
				Algorithms.streamCopy(fis, out);
				out.close();
				fis.close();
			}
			if(f.getName().endsWith(".xml")) {
				AugmentedDiffsInspector.main(new String[] { f.getAbsolutePath(), testResources.getAbsolutePath() });
			}
		}
		GenerateDailyObf.main(new String[] { testResources.getAbsolutePath() });
		if(unpackFolder != null) {
			for(File obfgz : new File(testResources, AugmentedDiffsInspector.DEFAULT_REGION).listFiles()) {
				if(obfgz.getName().endsWith(".obf.gz")) {
					GZIPInputStream is = new GZIPInputStream(new FileInputStream(obfgz));
					FileOutputStream out = new FileOutputStream(new File(unpackFolder, obfgz.getName().substring(0,
							obfgz.getName().length() - 3)));
					Algorithms.streamCopy(is, out);
					is.close();
					out.close();
				}
			}
		}
	}

	private static void generateObf(String[] subArgsArray, IndexCreator ic) throws IOException,
			SQLException, InterruptedException, XmlPullParserException {
		String fn = DataExtractionSettings.getSettings().getMapRenderingTypesFile();
		String regionName = subArgsArray[0];
		MapRenderingTypesEncoder types = new MapRenderingTypesEncoder(fn, regionName);
		ic.generateIndexes(new File(subArgsArray[0]),
				new ConsoleProgressImplementation(), null, MapZooms.getDefault(), types,
				log);
	}

	private static void printSynopsys() {
		System.out.println("This utility provides access to all other console utilities of OsmAnd,");
		System.out.println("each utility has own argument list and own synopsys. Here is the list:");
		System.out.println("\t\t generate-obf <path to osm file>: simple way to generate obf file in place. "
				+ "\t\t\t	Another supported options generate-map, generate-address, generate-poi, generate-roads (generate obf partially)");
		System.out.println("\t\t check-ocean-tile <lat> <lon> <zoom=11>: checks ocean or land tile is in bz2 list");
		System.out.println("\t\t generate-ocean-tile <coastline osm file> <optional output file>: creates ocean tiles 12 zoom");
		System.out.println("\t\t generate-java-style <pathtostyle> <pathtooutputfolder>: prints rendering style as java interpreted code");
		System.out.println("\t\t explain-rendering-style <pathtostyle>: prints explanation of the style");
		System.out.println("\t\t merge-flat-obf <path to result file> <paths to files to merge (>2)>: merges all data from 2+ obf files (address not supported)");
		System.out.println("\t\t split-obf <path_to_world_obf_diff> <path_to_result_folder> <path_to_regions.ocbf> <subfolder_name> <file_suffix>: splits a world_obf into obf region files");
		System.out.println("\t\t generate-obf-diff <path_old_obf> <path_new_obf> <name_or_path_diff_obf or stdout>: generates obf diff file between 2 obf files (address not supported), stdout prints to console");
		System.out.println("\t\t test-routing <own list of parameters>: helps to run routing test for specific locations");
		System.out.println("\t\t generate-ocbf <path to osmand/repos/ repository>: generates regions.ocbf file, this path should contain folders 'misc', 'tools', 'resources'");
		System.out.println("\t\t delete-unused-strings <path to repos/android/OsmAnd/res>: deletes unused translation in git repository (transforms all strings.xml)");
		System.out.println("\t\t extract-roads-only <path to full map obf file> : extracts .road.obf (road-only map) file from full .obf");
		System.out.println("\t\t generate-osmlive-tests <path_to_directory_with_resources_project> <optional_path_to_unpack_files>: test osmand live functionality");
		System.out.println("\t\t generate-region-tags <path to input osm file (osm, bz2, gz)> <path to output osm file> <path to ocbf file>: process osm file and assign tag osmand_region_name to every entity.");
		System.out.println("\t\t generate-ocean-tile-osm <optional path to osm file to write> <optional path to oceantiles_12.dat file>: generates ocean tiles osm file to check in JOSM ");
		System.out.println("\t\t merge-index " + BinaryMerger.helpMessage);
		System.out.println("\t\t compare " + BinaryComparator.helpMessage);
		System.out.println("\t\t generate-from-overpass <path to overpass.xml (must have format 2017_06_18-10_30)> <path to working directory> <path to regions.ocbf>: The utility converts overpass.xml to obf");
	}
}
