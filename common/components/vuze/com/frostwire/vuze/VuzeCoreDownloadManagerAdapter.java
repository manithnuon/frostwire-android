/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011-2014, FrostWire(R). All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.frostwire.vuze;

import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.download.impl.DownloadManagerAdapter;

import com.frostwire.logging.Logger;

/**
 * 
 * @author gubatron
 * @author aldenml
 *
 */
final class VuzeCoreDownloadManagerAdapter extends DownloadManagerAdapter {

    private static final Logger LOG = Logger.getLogger(VuzeCoreDownloadManagerAdapter.class);

    private final VuzeDownloadManager dm;
    private final VuzeDownloadListener listener;

    public VuzeCoreDownloadManagerAdapter(VuzeDownloadManager dm, VuzeDownloadListener listener) {
        this.dm = dm;
        this.listener = listener;
    }

    @Override
    public void stateChanged(DownloadManager manager, int state) {
        if (state == DownloadManager.STATE_READY) {
            manager.startDownload();
        } else {
            if (listener != null) {
                try {
                    listener.stateChanged(dm, state);
                } catch (Throwable e) {
                    LOG.error("Error calling download manager listener", e);
                }
            }
        }
    }

    @Override
    public void downloadComplete(DownloadManager manager) {
        if (listener != null) {
            try {
                listener.downloadComplete(dm);
            } catch (Throwable e) {
                LOG.error("Error calling download manager listener", e);
            }
        }
    }
}
