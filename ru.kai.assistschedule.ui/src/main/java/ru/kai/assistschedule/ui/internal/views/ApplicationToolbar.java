package ru.kai.assistschedule.ui.internal.views;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

//import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.registry.PerspectiveDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kai.assistschedule.ui.internal.widgets.ImageCache;


public class ApplicationToolbar extends ContributionItem {

    protected static final Logger LOG = LoggerFactory.getLogger(ApplicationToolbar.class);
    
    private static final String KEY_PERSPECTIVE = "perspective";
    
    @Override
    public void fill(ToolBar parent, int index) {
        LOG.info("Создание главного тулбара приложения");

        final IPerspectiveDescriptor[] perspectiveDescriptors = 
                PlatformUI.getWorkbench().getPerspectiveRegistry().getPerspectives();


        Arrays.sort(perspectiveDescriptors, new Comparator<IPerspectiveDescriptor>() {
            public int compare(IPerspectiveDescriptor o1, IPerspectiveDescriptor o2) {
                PerspectiveDescriptor pd1 = (PerspectiveDescriptor) o1;
                PerspectiveDescriptor pd2 = (PerspectiveDescriptor) o2;

                String sort1 = pd1.getConfigElement().getAttribute("sort");
                String sort2 = pd2.getConfigElement().getAttribute("sort");
                LOG.debug(String.format("sort1 = %s, sort2 = %s", sort1, sort2));
                if (sort1 == null || sort1.isEmpty()) return 1;
                if (sort2 == null || sort2.isEmpty()) return -1;
                return sort1.compareTo(sort2);
            }

        });

        LOG.info("perspectiveDescriptors.length = {}", perspectiveDescriptors.length);
        for (IPerspectiveDescriptor perspectiveDescriptor : perspectiveDescriptors) {

            PerspectiveDescriptor pd = (PerspectiveDescriptor) perspectiveDescriptor;
            
            LOG.info("PerspectiveDescriptor pd = {}", pd.getLabel());
            
            String menuVisible = pd.getConfigElement().getAttribute("menuVisible");

            if (!pd.getConfigElement().getAttribute("class").startsWith("ru.kai")) continue;
            
            
            
//            try {
//                if (!Boolean.valueOf(menuVisible)) continue;
//            } catch (Exception e) {
//                continue;
//            }


            final ToolItem toolItem = new ToolItem(parent, SWT.PUSH);
            toolItem.setToolTipText(perspectiveDescriptor.getDescription());

            final Image image = perspectiveDescriptor.getImageDescriptor().createImage();
            toolItem.setData(KEY_PERSPECTIVE, perspectiveDescriptor);
            if ("ru.kai.assistantschedule.setting.perspective".equals(
            		((PerspectiveDescriptor)perspectiveDescriptor).getConfigElement().getAttribute("id"))) {
            	changeImage(toolItem, ((PerspectiveDescriptor)perspectiveDescriptor)
            			.getConfigElement().getAttribute("icon"));
            } else {
            	toolItem.setImage(image);            	
            }

            toolItem.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent e) {
                    ToolItem toolItem = (ToolItem) e.widget;

                    if (toolItem.getImage() != null && !toolItem.getImage().isDisposed()) {
                        toolItem.getImage().dispose();
                    }
                }
            });

            toolItem.addSelectionListener(new PerspectiveSelectionAction());

        }
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
    
    private static class PerspectiveSelectionAction extends SelectionAdapter {

        @Override
        public void widgetSelected(SelectionEvent e) {
            IPerspectiveDescriptor perspectiveDescriptor = (IPerspectiveDescriptor) e.widget
                    .getData(KEY_PERSPECTIVE);

            changeImage((ToolItem)e.getSource(), ((PerspectiveDescriptor)perspectiveDescriptor).getConfigElement().getAttribute("icon"));
            
            ICommandService commandService = (ICommandService) PlatformUI.getWorkbench()
                    .getService(ICommandService.class);

            Command command = commandService.getCommand("org.eclipse.ui.perspectives.showPerspective");


            Map parameters = new HashMap();
            parameters.put("org.eclipse.ui.perspectives.showPerspective.perspectiveId", perspectiveDescriptor.getId());
            ParameterizedCommand parameterizedCommand = ParameterizedCommand.generateCommand(command, parameters);

            IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench()
                    .getService(IHandlerService.class);
            try {
                handlerService.executeCommand(parameterizedCommand, null);
            } catch (ExecutionException ex) {
//                StatusManager.getManager().handle(new Status(IStatus.ERROR, ApplicationPlugin.PLUGIN_ID, ex.getMessage(), ex));
            } catch (NotDefinedException ex) {
//                StatusManager.getManager().handle(new Status(IStatus.ERROR, ApplicationPlugin.PLUGIN_ID, ex.getMessage(), ex));
            } catch (NotEnabledException ex) {
//                StatusManager.getManager().handle(new Status(IStatus.ERROR, ApplicationPlugin.PLUGIN_ID, ex.getMessage(), ex));
            } catch (NotHandledException ex) {
//                StatusManager.getManager().handle(new Status(IStatus.ERROR, ApplicationPlugin.PLUGIN_ID, ex.getMessage(), ex));
            }
        }
        
    }

    private static void changeImage(final ToolItem item, final String imgPath) {
    	item.getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				if(null != prevItem && null != prevImgPath) {
					prevItem.setImage(ImageCache.getImage(prevImgPath));
		    	}
				if(item.isDisposed()) {
					return;
				}
		    	item.setImage(ImageCache.getImage(
		    			imgPath.substring(0, imgPath.length() - 4) + "_selected.png"));
		    	prevItem = item;
		    	prevImgPath = imgPath;
			}
		});
    }
    
    private static ToolItem prevItem;
    private static String prevImgPath;
}
