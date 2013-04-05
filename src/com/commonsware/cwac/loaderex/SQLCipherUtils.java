/*
 * Copyright (c) 2012 CommonsWare, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.commonsware.cwac.loaderex;

import android.content.Context;
import java.io.File;
import java.io.IOException;
import net.sqlcipher.database.SQLiteDatabase;

public class SQLCipherUtils {
  public static enum State {
    DOES_NOT_EXIST, UNENCRYPTED, ENCRYPTED, UNKNOWN
  }

  public static State getDatabaseState(Context context, String dbName) {
    File dbPath=context.getDatabasePath(dbName);

    if (dbPath.exists()) {
      SQLiteDatabase db=null;

      try {
        db=
            SQLiteDatabase.openDatabase(dbPath.getAbsolutePath(), "",
                                        null,
                                        SQLiteDatabase.OPEN_READONLY);

        db.getVersion();

        return(State.UNENCRYPTED);
      }
      catch (Exception e) {
        return(State.ENCRYPTED);
      }
      finally {
        if (db != null) {
          db.close();
        }
      }
    }

    return(State.DOES_NOT_EXIST);
  }

  public static void encrypt(Context ctxt, String dbName,
                             String passphrase) throws IOException {
    File originalFile=ctxt.getDatabasePath(dbName);

    if (originalFile.exists()) {
      File newFile=
          File.createTempFile("sqlcipherutils", "tmp",
                              ctxt.getCacheDir());
      SQLiteDatabase db=
          SQLiteDatabase.openDatabase(originalFile.getAbsolutePath(),
                                      "", null,
                                      SQLiteDatabase.OPEN_READWRITE);

      db.rawExecSQL(String.format("ATTACH DATABASE '%s' AS encrypted KEY '%s';",
                                  newFile.getAbsolutePath(), passphrase));
      db.rawExecSQL("SELECT sqlcipher_export('encrypted')");
      db.rawExecSQL("DETACH DATABASE encrypted;");

      int version=db.getVersion();

      db.close();

      db=
          SQLiteDatabase.openDatabase(newFile.getAbsolutePath(),
                                      passphrase, null,
                                      SQLiteDatabase.OPEN_READWRITE);
      db.setVersion(version);
      db.close();

      originalFile.delete();
      newFile.renameTo(originalFile);
    }
  }
}
