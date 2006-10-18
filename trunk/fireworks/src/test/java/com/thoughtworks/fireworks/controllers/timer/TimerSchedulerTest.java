/*
 *    Copyright (c) 2006 LiXiao.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.thoughtworks.fireworks.controllers.timer;

import com.thoughtworks.fireworks.core.developer.ReschedulableTask;
import com.thoughtworks.turtlemock.Mock;
import com.thoughtworks.turtlemock.Turtle;
import junit.framework.TestCase;

import java.util.Timer;

public class TimerSchedulerTest extends TestCase {
    private static final int LONG_TIME = 10000;

    private Mock task;
    private TimerTaskManager manager;
    private TimerScheduler scheduler;
    private Timer timer;

    protected void setUp() throws Exception {
        task = Turtle.mock(ReschedulableTask.class);
        manager = new TimerTaskManager(TestUtils.createTimerTaskFactory());
        scheduler = new TimerScheduler(manager);
        timer = new Timer();
    }

    public void testShouldSetDelayTimeFirstBeforeSchedule() throws Exception {
        scheduler.schedule(timer, (ReschedulableTask) task.mockTarget());
        assertFalse(manager.cancelTask());

        scheduler.setDelayTime(LONG_TIME);
        scheduler.schedule(timer, (ReschedulableTask) task.mockTarget());
        assertTrue(manager.cancelTask());
    }

    public void testShouldNotScheduleTaskDirectlyAfterCabinetActionFired() throws Exception {
        scheduler.actionFinished();
        scheduler.setDelayTime(LONG_TIME);
        scheduler.schedule(timer, (ReschedulableTask) task.mockTarget());
        assertFalse(manager.cancelTask());
    }

    public void testShouldCancelTasksWhenActionStarted() throws Exception {
        scheduler.setDelayTime(LONG_TIME);
        scheduler.schedule(timer, (ReschedulableTask) task.mockTarget());
        scheduler.actionStarted();

        assertFalse(manager.cancelTask());
    }

    public void testShouldNotScheduleTaskIfDelayTimeIsLessThanZero() throws Exception {
        scheduler.setDelayTime(-100);
        scheduler.schedule(timer, (ReschedulableTask) task.mockTarget());
        assertFalse(manager.cancelTask());
    }

    public void testCancelTasksInTaskManager() throws Exception {
        scheduler.setDelayTime(LONG_TIME);
        scheduler.schedule(timer, (ReschedulableTask) task.mockTarget());
        scheduler.cancelTasks();

        assertFalse(manager.cancelTask());
    }
}
