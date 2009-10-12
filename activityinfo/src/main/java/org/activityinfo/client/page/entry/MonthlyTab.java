package org.activityinfo.client.page.entry;

import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;
import org.activityinfo.client.Application;

public class MonthlyTab extends TabItem {


    public MonthlyTab(MonthlyGrid grid) {
        setText(Application.CONSTANTS.monthlyReports());
        setIcon(Application.ICONS.table());
        setLayout(new FitLayout());
        add(grid);

    }

    @Override
    public void render(Element target, int index) {
        super.render(target, index);
    }
}
