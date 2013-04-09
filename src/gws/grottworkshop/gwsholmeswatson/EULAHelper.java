package gws.grottworkshop.gwsholmeswatson;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;

import com.actionbarsherlock.R;

import android.content.DialogInterface;





public class EULAHelper {

	public EULAHelper() {
		// TODO Auto-generated constructor stub
	}
	
	public static boolean hasAcceptedEula() {
		boolean hasAccepted = GWSPreferences.getEULAState(GWSApplication.getAppContext());
		return hasAccepted;
	}
	
	public static void setAcceptedEula() {
		GWSPreferences.getEULAAcceptState(GWSApplication.getAppContext());
	}

	public static void showEula(final boolean accepted, final Activity activity) {
        AlertDialog.Builder eula = new AlertDialog.Builder(activity)
                .setTitle(R.string.gws_eula_title)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage(R.string.gws_eula_text)
                .setCancelable(accepted);

        if (accepted) {
            // If they've accepted the EULA allow, show an OK to dismiss.
            eula.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        } else {
            // If they haven't accepted the EULA allow, show accept/decline buttons and exit on
            // decline.
            eula
                    .setPositiveButton(R.string.gws_accept,
                            new android.content.DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    setAcceptedEula();
                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton(R.string.gws_decline,
                            new android.content.DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    activity.finish();
                                }
                            });
        }
        eula.show();
    }
	
	
}
