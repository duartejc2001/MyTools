import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

import com.sun.media.jfxmedia.MediaPlayer;
import javafx.scene.media.Media;
import  sun.audio.*;    //import the sun.audio package
import  java.io.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.nio.file.*;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import static java.nio.file.StandardCopyOption.*;
import java.nio.channels.FileChannel;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


public class Main {


    public static boolean show = false;
    public static boolean number_files = false;


    public static void main(String[] args) {


        Musica m;
        File f;

        /* With a unique parameter just get a list of all music files */
        if (args.length == 1) {
            show = true;
            m = new Musica(args[0]);
        }
        /* With two parameters, decide between shuffle or not, according the second parameter */
        if (args.length == 2) {
            f = new File(args[1]);
            if (f.exists() && f.isFile())
                m = new Musica(args[0], args[1], "");  /* String, input text files with music ids to play */
            else {
                m = new Musica(args[0], Integer.parseInt(args[1]), "");  /* integer, number the musics to shuffle */
            }
        }
        /* With three parameters, do the same as two, but copy instead play */
        if (args.length == 3) {
            f = new File(args[1]);
            if (f.exists() && f.isFile())
                m = new Musica(args[0], args[1], args[2]);
            else
                m = new Musica(args[0], Integer.parseInt(args[1]), args[2]);
        }
        /* With four parameters, do the same as three, but number the files if it's the case */
        if (args.length == 4) {
            if (args[3].equals("-n")) Main.number_files=true;
            f = new File(args[1]);
            if (f.exists() && f.isFile())
                m = new Musica(args[0], args[1], args[2]);
            else
                m = new Musica(args[0], Integer.parseInt(args[1]), args[2]);
        }
    }
}
        class Musica {
            String diretorio;
            static int max = 1000;
            static int music_count;
            static int music_total;
            static File[] AllFiles = new File[max];
            File[] files;
            int increase = 500;
            int maxpos = 300;
            int ids_quantity;
            int increasepos = 100;
            int[] AllPos = new int[maxpos];
            int index = 0;
            int flag_music_copy = 0;


            public Musica(String dir) {    /* Initialization, getting files and number of musics in Source folder*/
                music_total = 0;
                System.out.println("Searching all music files in folder and subfolders ...\n");
                getdir(dir);
                music_count = music_total;
                System.out.println("\nDone. "+ music_count+ " musics found!\n\n");
            }


            public Musica(String dir, int number, String target) {    /* Setting number of musics, target folder, random */
                Musica m = new Musica(dir);
                select_musics(m, number, target, AllPos);
            }

            public Musica(String dir, String ids, String target) {    /* Setting number of musics, target folder, id list */
                Musica m = new Musica(dir);
                try {
                    getIDS(ids);   /* maxpos get number of ids */
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                select_musics(m, -1, target, AllPos);        /* number = -1 when not random */
            }


            int attrib_int(int i, int v) {  /* Allocate more memory to ids vector if needed, if not just set value to array */
                if (i < maxpos) {
                    AllPos[i] = v;
                } else {
                    int[] temp = new int[maxpos];
                    int j;
                    for (j = 0; j < maxpos; j++) temp[j] = AllPos[j];
                    AllPos = new int[maxpos + increasepos];
                    for (j = 0; j < maxpos; j++) AllPos[j] = temp[j];
                    maxpos = maxpos + increase;
                    AllPos[i] = v;
                }
                return (0);
            }

            int attrib(int i, File v) {  /* Allocate more memory to File vector if needed, if not just set value to array */
                if (i < max) {
                    AllFiles[i] = v;
                } else {
                    File[] temp = new File[max];
                    int j;
                    for (j = 0; j < max; j++) temp[j] = AllFiles[j];
                    AllFiles = new File[max + increase];
                    for (j = 0; j < max; j++) AllFiles[j] = temp[j];
                    max = max + increase;
                    AllFiles[i] = v;
                }
                return (0);
            }


            int getIDS(String ids) throws IOException {      /* Get IDs from Ids input file */
                String lines="";
                String aux;
                int i = 0, j = 0, k = 0;

                BufferedReader br = new BufferedReader(new FileReader(ids));  /* Read all file adding to string */
                try {
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();
                    while (line != null) {
                        sb.append(line);
                        sb.append(System.lineSeparator());
                        line = br.readLine();
                    }
                    lines = sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    br.close();
                }

                /* Add each each id to array and get the number of ids */
                i = lines.indexOf("id#:[");
                k = lines.indexOf("]");
                while (i >= 0) {
                    /* AllPos[j] = Integer.parseInt(lines.substring(i+5,k));*/
                    attrib_int(j, Integer.parseInt(lines.substring(i+5,k)));
                    lines = lines.substring(k+1);
                    i = lines.indexOf("id#:[");
                    if (i>=0)
                    {
                        aux = lines.substring(i);
                        k = aux.indexOf("]") + i;
                    }
                    j++;

                }
                ids_quantity=j;
                return (0);
            }

            int getdir(String dir) {     /* Go through folder and subfolder and get all wma and mp3 files */
                diretorio = dir;
                String fname, ext;
                int i;
                File folder = new File(dir);
                files = folder.listFiles();
                for (File file : files) {    /* Go to each MP3 or WMA file in each folder */
                    if (!file.isDirectory()) {
                        fname = file.getName();
                        ext = fname.substring(fname.length() - 3);
                        if ((Objects.equals(ext.toLowerCase(), "wma")) | (Objects.equals(ext.toLowerCase(), "mp3"))) {
                            if (Main.show) {
                                System.out.println("id#:["+music_total+"]\t"+ dir + "\\" + file.getName());
                            }
                            music_total++;
                            attrib(index, file);/* AllFiles[index]=file; */
                            index++;
                        }
                    }
                    if (file.isDirectory()) getdir(dir + "\\" + file.getName());
                }
                music_count = music_total;
                return (0);
            }

            String zerofill (int i, int j)  /* Adds zero to the left to all numbers be with same number of digits */
            {
                String r,r2;

                r=Integer.toString(i);
                r2=Integer.toString(j);

                while (r.length()<r2.length())
                {
                    r="0"+r;
                }
                return (r);
            }
            /* Play or copy musics according */
            int select_musics(Musica mus, int n, String target_Folder, int ids_file[]) {
                File temp;
                File target;
                File mpos;
                int pos, i;
                boolean copy_files = false;
                boolean aleatory = true;

                target = new File(target_Folder);
                if (target.exists()) copy_files = true;

                if (n == -1)  /* n=-1 when selection is not random. */ {
                    n = ids_quantity;
                    aleatory = false;
                }

                for (i = 0; i < n; i++) {    /* For each music from list or to be selected randomly */
                    System.out.println("\n" + (i + 1) + "/" + n);
                    if (music_count == 1)    /* if only one music remaining, play this one and select all again */
                    {
                        pos = 0;
                        temp = AllFiles[pos];
                        System.out.println(temp.getName());
                        System.out.println(temp.getAbsolutePath());
                        music_count = music_total;
                    }
                    else
                        {
                        if (aleatory) {
                            Random rand = new Random();    /* Select one music randomly */
                            pos = rand.nextInt(music_count);
                            System.out.println("id#: " + pos);
                            temp = AllFiles[pos];
                            music_count--;             /* Exchange selected music with last one and reduce list size to not repeat */
                            AllFiles[pos] = AllFiles[music_count];
                            AllFiles[music_count] = temp;
                            System.out.println(temp.getName());
                            System.out.println(temp.getAbsolutePath());

                        } else {
                            pos = ids_file[i];           /* not random, just take file from array */
                            System.out.println("id#: " + pos);
                            temp = AllFiles[pos];
                            System.out.println(temp.getName());
                            System.out.println(temp.getAbsolutePath());
                        }
                    }
                    Path FROM = Paths.get(temp.getAbsolutePath());

                    String ext = temp.getName().substring(temp.getName().length() - 3);  /* Set extension according */

                    if ((Objects.equals(ext.toLowerCase(), "mp3")))
                        ext = "mp3";
                    else
                        ext = "wma";

                    if (copy_files) {     /* when copying if same filename exist, copy to a different folder */
                        String path_text = target_Folder + "\\" + temp.getName();
                        if (Main.number_files) path_text = target_Folder + "\\(" + zerofill(i+1,n) + ")"+ temp.getName();
                        target = new File(path_text);
                        int j = 0;
                        while (target.exists()) {
                            j++;
                            target = new File(target_Folder + "\\" + j);
                            if (!target.exists()) target.mkdir();
                            path_text = target_Folder + "\\" + j + "\\" + temp.getName();
                            target = new File(path_text);
                        }
                        Path TO = Paths.get(path_text);
                        CopyOption[] options = new CopyOption[]{
                                StandardCopyOption.REPLACE_EXISTING,
                                StandardCopyOption.COPY_ATTRIBUTES};
                        try {
                            Files.copy(FROM, TO, options);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Path TO = Paths.get("music1." + ext);     /* to play, copy file to a temporary file to avoid issues due name*/
                        if (flag_music_copy == 0) TO = Paths.get("music2." + ext);
                        //overwrite existing file, if exists
                        CopyOption[] options = new CopyOption[]{
                                StandardCopyOption.REPLACE_EXISTING,
                                StandardCopyOption.COPY_ATTRIBUTES};
                        try {
                            Files.copy(FROM, TO, options);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            PrintWriter writer = new PrintWriter("playmusic.bat", "ISO-8859-1");
                            if (flag_music_copy == 1) {
                                writer.println("music1." + ext);
                                File f = new File("music1." + ext);
                                f.setWritable(true);
                            }
                            if (flag_music_copy == 0) {
                                writer.println("music2." + ext);
                                File f = new File("music2." + ext);
                                f.setWritable(true);
                            }

                            flag_music_copy = (flag_music_copy + 1) % 2; /* Exchange between 1 and 2 */

                            writer.close();
                            Runtime.getRuntime().exec("playmusic.bat");   /* Play Music */
                            Thread.sleep(250);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Scanner in = new Scanner(System.in);
                        int s;
                        s = -1;

                        while ((i < n - 1) && (s == -1)) {
                            System.out.print("Type any number greater than 0 to next music or 0 to exit: ");  /* Wait user select next one */
                            s = in.nextInt();

                        }
                        ;
                        if (s == 0) break;
                    }
                }
                return (0);
            }

        }



