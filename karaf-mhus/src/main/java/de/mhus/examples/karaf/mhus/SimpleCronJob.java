package de.mhus.examples.karaf.mhus;

import java.util.Date;

import org.osgi.service.component.annotations.Component;

import de.mhus.lib.annotations.util.Interval;
import de.mhus.lib.core.MDate;
import de.mhus.lib.core.aaa.Aaa;
import de.mhus.osgi.api.scheduler.SchedulerService;
import de.mhus.osgi.api.scheduler.SchedulerServiceAdapter;

@Component(service = SchedulerService.class)
@Interval(value = "10s", runAs = "admin")
public class SimpleCronJob extends SchedulerServiceAdapter {

    private int cnt;

    @Override
    public void run(Object environment) throws Exception {
        cnt++;
        System.out.println("###CRON### " + MDate.toIso8601(new Date()) + " " + " " + cnt + " " +Aaa.getPrincipal() );
    }

}
