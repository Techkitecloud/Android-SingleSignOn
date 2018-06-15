package de.luhmer.owncloud.accountimporter.exceptions;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;

import de.luhmer.owncloud.accountimporter.R;
import de.luhmer.owncloud.accountimporter.model.ExceptionMessage;

public class SSOException extends Exception {

    private ExceptionMessage em;

    public SSOException() {
        super("Single Sign On Exception (use getMessage(context) for more information)");
    }

    private void loadExceptionMessage(Context context) {
        em = new ExceptionMessage();

        Log.d("AAAAAA", "this.getClass(): " + this.getClass());

        if(this instanceof CurrentAccountNotFoundException) {
            em.title   = context.getString(R.string.current_account_not_found_exception_title);
            em.message = context.getString(R.string.current_account_not_found_exception_message);
        } else if(this instanceof NoCurrentAccountSelectedException) {
            em.title   = context.getString(R.string.no_current_account_selected_exception_title);
            em.message = context.getString(R.string.no_current_account_selected_exception_message);
        } else if(this instanceof TokenMismatchException) {
            em.title   = context.getString(R.string.token_mismatch_title);
            em.message = context.getString(R.string.token_mismatch_message);
        } else if(this instanceof NextcloudFilesAppNotInstalledException) {
            em.title   = context.getString(R.string.nextcloud_files_app_not_installed_title);
            em.message = context.getString(R.string.nextcloud_files_app_not_installed_message);
        } else if(this instanceof NextcloudFilesAppAccountNotFoundException) {
            em.title   = context.getString(R.string.nextcloud_files_app_account_not_found_title);
            em.message = context.getString(R.string.nextcloud_files_app_account_not_found_message);
        } else {
            em.title = "Unknown error";
            em.message = "Unknown error..";
        }
    }

    public String getTitle(Context context) {
        if(em == null) {
            loadExceptionMessage(context);
        }
        return em.title;
    }

    public String getMessage(Context context) {
        if(em == null) {
            loadExceptionMessage(context);
        }
        return em.message;
    }

    @Override
    public String getMessage() {
        // If already loaded.. return it
        if(em != null) {
            return em.message;
        }

        // Otherwise try to get the application via reflection
        Application app = getApplication();
        if(app != null) {
            loadExceptionMessage(app);
            return em.message;
        }

        // If that didn't work.. well return the "generic" base message
        return super.getMessage();
    }

    private Application getApplication() {
        try {
            return (Application) Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication").invoke(null, (Object[]) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
