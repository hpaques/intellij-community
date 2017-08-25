/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.build;

import com.intellij.build.events.BuildEvent;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.AnsiEscapeDecoder;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

/**
 * @author Vladislav.Soroka
 */
public class BuildTextConsoleView extends ConsoleViewImpl implements BuildConsoleView, AnsiEscapeDecoder.ColoredTextAcceptor {
  private final String myId;
  private final AnsiEscapeDecoder myAnsiEscapeDecoder = new AnsiEscapeDecoder();

  public BuildTextConsoleView(@NotNull Project project, boolean viewer, String id) {
    super(project, viewer);
    myId = id;
  }

  @Override
  public String getViewId() {
    return myId;
  }

  @Override
  public void onEvent(BuildEvent event) {
    myAnsiEscapeDecoder.escapeText(event.getMessage(), ProcessOutputTypes.STDOUT, this);
  }

  @Override
  public void coloredTextAvailable(@NotNull String text, @NotNull Key attributes) {
    print(text, ConsoleViewContentType.getConsoleViewType(attributes));
  }
}

