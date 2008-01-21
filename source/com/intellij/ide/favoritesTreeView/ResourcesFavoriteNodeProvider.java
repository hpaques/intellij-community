/*
 * User: anna
 * Date: 21-Jan-2008
 */
package com.intellij.ide.favoritesTreeView;

import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.lang.properties.ResourceBundle;
import com.intellij.lang.properties.ResourceBundleImpl;
import com.intellij.lang.properties.projectView.ResourceBundleNode;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.ex.DataConstantsEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResourcesFavoriteNodeProvider extends FavoriteNodeProvider {
  private final Project myProject;

  public ResourcesFavoriteNodeProvider(Project project) {
    myProject = project;
  }

  public Collection<AbstractTreeNode> getFavoriteNodes(final DataContext context, final ViewSettings viewSettings) {
    final Project project = PlatformDataKeys.PROJECT.getData(context);
    if (project == null) return null;
    final Object object = context.getData(DataConstantsEx.RESOURCE_BUNDLE_ARRAY);
    //on bundles nodes
       if (object instanceof ResourceBundle[]) {
         final Collection<AbstractTreeNode> result = new ArrayList<AbstractTreeNode>();
         for (ResourceBundle bundle : (ResourceBundle[])object) {
           result.add(new ResourceBundleNode(project, bundle, viewSettings));
         }
         return result;
       }
    return null;
  }

  public boolean elementContainsFile(final Object element, final VirtualFile vFile) {
    if (element instanceof ResourceBundle) {
      ResourceBundle bundle = (ResourceBundle)element;
      final List<PropertiesFile> propertiesFiles = bundle.getPropertiesFiles(myProject);
      for (PropertiesFile file : propertiesFiles) {
        final VirtualFile virtualFile = file.getVirtualFile();
        if (virtualFile == null) continue;
        if (vFile.getPath().equals(virtualFile.getPath())) {
          return true;
        }
      }
    }
    return false;
  }

  public int getElementWeight(final Object element, final boolean isSortByType) {
    return -1;
  }

  public String getElementLocation(final Object element) {
    return null;
  }

  public boolean isInvalidElement(final Object element) {
    if (element instanceof ResourceBundle) {
      ResourceBundle resourceBundle = (ResourceBundle)element;
      List<PropertiesFile> propertiesFiles = resourceBundle.getPropertiesFiles(myProject);
      if (propertiesFiles.size() == 1) {
        //todo result.add(new PsiFileNode(myProject, propertiesFiles.iterator().next(), this));
        return true;
      }
    }
    return false;
  }

  @NotNull
  public String getFavoriteTypeId() {
    return "resource_bundle";
  }

  public String getElementUrl(final Object element) {
    if (element instanceof ResourceBundle) {
      return ((ResourceBundleImpl)element).getUrl();
    }
    return null;
  }

  public String getElementModuleName(final Object element) {
    return null;
  }

  public Object[] createPathFromUrl(final Project project, final String url, final String moduleName) {
    return new Object[]{ResourceBundleImpl.createByUrl(url)};
  }
}