/*
 * This file is part of ActivityInfo.
 *
 * ActivityInfo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ActivityInfo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ActivityInfo.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2009 Alex Bertram and contributors.
 */

package org.sigmah.server.report.renderer;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.sigmah.server.report.renderer.excel.ExcelMapDataExporter;
import org.sigmah.server.report.renderer.excel.ExcelReportRenderer;
import org.sigmah.server.report.renderer.image.ImageReportRenderer;
import org.sigmah.server.report.renderer.itext.PdfReportRenderer;
import org.sigmah.server.report.renderer.itext.RtfReportRenderer;
import org.sigmah.server.report.renderer.ppt.PPTRenderer;
import org.sigmah.shared.command.RenderElement;

/**
 * @author Alex Bertram
 */
public class RendererFactory {

    private final Injector injector;

    @Inject
    public RendererFactory(Injector injector) {
        this.injector = injector;
    }

    public Renderer get(RenderElement.Format format) {

        switch(format) {
            case PowerPoint:
                return injector.getInstance(PPTRenderer.class);
            case Word:
                return injector.getInstance(RtfReportRenderer.class);
            case Excel:
                return injector.getInstance(ExcelReportRenderer.class);
            case PDF:
                return injector.getInstance(PdfReportRenderer.class);
            case PNG:
                return injector.getInstance(ImageReportRenderer.class);
            case Excel_Data:
                return injector.getInstance(ExcelMapDataExporter.class);
        }

        throw new IllegalArgumentException();
    }
}