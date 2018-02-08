package org.mikko.unrooter;

import android.content.Context;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;

/** Temporarily roots and unroots the device
 */
class StatusChanger {
    /** Roots the device if it has been previously unrooted by the unroot() function.
     *
     * @param context Context to access string resources. Allows for potentially translatable error messages.
     * @return String describing a possible error happening during the process or an empty string if
     * everything went well.
     */
    public static String root(Context context){
        int state = 0;
        String message = "";
        try{
            Process suProc = Runtime.getRuntime().exec("subackup");
            DataOutputStream commandOutput = new DataOutputStream(suProc.getOutputStream());

            commandOutput.writeBytes("mount -o rw,remount /system\n");
            commandOutput.flush();
            ++state;

            commandOutput.writeBytes("mv /system/xbin/subackup /system/xbin/su\n");
            commandOutput.flush();
            ++state;

            commandOutput.writeBytes("mv /system/bin/subackup /system/bin/su\n");
            commandOutput.flush();
            ++state;

            commandOutput.writeBytes("mount -o ro,remount /system\n");
            commandOutput.flush();
            ++state;

            commandOutput.writeBytes("exit\n");
            commandOutput.flush();

            suProc.waitFor();
            suProc.destroy();
            commandOutput.close();

            suProc = Runtime.getRuntime().exec("su");
            commandOutput = new DataOutputStream(suProc.getOutputStream());

            commandOutput.writeBytes("mv /system/bin/.ext/.subackup /system/bin/.ext/.su\n");
            commandOutput.flush();
            ++state;

            commandOutput.writeBytes("exit\n");
            commandOutput.flush();

            suProc.waitFor();

        }catch(IOException | InterruptedException e){
            message = e.getMessage();
        }

        Log.w(String.valueOf(state), message);

        if(state == 4){
            message = "?noRoot";
        }else if(state < 4){
            try {
                reverseRoot(state);
                message = context.getString(R.string.error_rooting_failed);
            }catch(IOException | InterruptedException e){
                message = e.getMessage();
            }
        }

        return message;
    }

    /** Unroots the device.
     *
     * @param context Context to access string resources. Allows for potentially translatable error messages.
     * @return String describing a possible error happening during the process or an empty string if
     * everything went well.
     */
    public static String unRoot(Context context){
        int state = 0;
        String message = "";
        try{
            Process suProc = Runtime.getRuntime().exec("su");
            DataOutputStream commandOutput = new DataOutputStream(suProc.getOutputStream());

            commandOutput.writeBytes("mount -o rw,remount /system\n");
            commandOutput.flush();
            ++state;

            commandOutput.writeBytes("mv /system/xbin/su /system/xbin/subackup\n");
            commandOutput.flush();
            ++state;

            commandOutput.writeBytes("mv /system/bin/su /system/bin/subackup\n");
            commandOutput.flush();
            ++state;

            commandOutput.writeBytes("mount -o ro,remount /system\n");
            commandOutput.flush();

            commandOutput.writeBytes("exit\n");
            commandOutput.flush();

            suProc.waitFor();
            suProc.destroy();
            commandOutput.close();

            suProc = Runtime.getRuntime().exec("subackup");
            commandOutput = new DataOutputStream(suProc.getOutputStream());

            commandOutput.writeBytes("mv /system/bin/.ext/.su /system/bin/.ext/.subackup\n");
            commandOutput.flush();
            ++state;

            commandOutput.writeBytes("exit\n");
            commandOutput.flush();

            suProc.waitFor();

        }catch(IOException | InterruptedException e){
            message = e.getMessage();
        }

        Log.w(String.valueOf(state), message);

        if(state < 4){
            try {
                reverseUnRoot(state);
                message = context.getString(R.string.error_unrooting_failed);
            }catch(IOException | InterruptedException e){
                message = e.getMessage();
            }
        }

        return message;
    }

    /** Tries to reverse the root() functions actions if it produces an error.
     *
     * @param state The state in which the error happened
     * @throws IOException From Process.waitFor()
     * @throws InterruptedException From Process.waitFor()
     */
    private static void reverseRoot(int state) throws IOException, InterruptedException{
        // State 4 is not handled as there is only a exit command after that and as such there is
        // nothing to reverse.
        if(state >= 3) {
            Process suProc = Runtime.getRuntime().exec("su");
            DataOutputStream commandOutput = new DataOutputStream(suProc.getOutputStream());

            commandOutput.writeBytes("mount -o rw,remount /system\n");
            commandOutput.flush();

            commandOutput.writeBytes("mv /system/bin/su /system/bin/subackup\n");
            commandOutput.flush();

            commandOutput.writeBytes("mount -o ro,remount /system\n");
            commandOutput.flush();

            commandOutput.writeBytes("exit\n");
            commandOutput.flush();

            suProc.waitFor();
        }
        if(state >= 2){
            Process suProc = Runtime.getRuntime().exec("subackup");
            DataOutputStream commandOutput = new DataOutputStream(suProc.getOutputStream());

            commandOutput.writeBytes("mount -o rw,remount /system\n");
            commandOutput.flush();

            commandOutput.writeBytes("mv /system/xbin/su /system/xbin/subackup\n");
            commandOutput.flush();

            commandOutput.writeBytes("mount -o ro,remount /system\n");
            commandOutput.flush();

            commandOutput.writeBytes("exit\n");
            commandOutput.flush();

            suProc.waitFor();
        }
    }

    /** Tries to reverse the unroot() functions actions if it produces an error.
     *
     * @param state The state in which the error happened
     * @throws IOException From Process.waitFor()
     * @throws InterruptedException From Process.waitFor()
     */
    private static void reverseUnRoot(int state) throws IOException, InterruptedException{
        // State 4 is not handled as there is only a exit command after that and as such there is
        // nothing to reverse.
        if(state >= 3) {
            Process suProc = Runtime.getRuntime().exec("subackup");
            DataOutputStream commandOutput = new DataOutputStream(suProc.getOutputStream());

            commandOutput.writeBytes("mount -o rw,remount /system\n");
            commandOutput.flush();

            commandOutput.writeBytes("mv /system/bin/subackup /system/bin/su\n");
            commandOutput.flush();

            commandOutput.writeBytes("mount -o ro,remount /system\n");
            commandOutput.flush();

            commandOutput.writeBytes("exit\n");
            commandOutput.flush();

            suProc.waitFor();
        }
        if(state >= 2){
            Process suProc = Runtime.getRuntime().exec("su");
            DataOutputStream commandOutput = new DataOutputStream(suProc.getOutputStream());

            commandOutput.writeBytes("mount -o rw,remount /system\n");
            commandOutput.flush();

            commandOutput.writeBytes("mv /system/xbin/subackup /system/xbin/su\n");
            commandOutput.flush();

            commandOutput.writeBytes("mount -o ro,remount /system\n");
            commandOutput.flush();

            commandOutput.writeBytes("exit\n");
            commandOutput.flush();

            suProc.waitFor();
        }
    }
}
