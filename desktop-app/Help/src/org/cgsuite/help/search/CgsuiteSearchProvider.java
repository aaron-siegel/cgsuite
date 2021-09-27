/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.help.search;

import java.util.Collections;
import org.cgsuite.help.CgsuiteHelpTopComponent;
import org.cgsuite.help.HelpIndex;
import org.netbeans.spi.quicksearch.SearchProvider;
import org.netbeans.spi.quicksearch.SearchRequest;
import org.netbeans.spi.quicksearch.SearchResponse;
import scala.collection.JavaConverters;

public class CgsuiteSearchProvider implements SearchProvider {

    @Override
    public void evaluate(SearchRequest request, SearchResponse response) {

        for (HelpIndex.Result result : JavaConverters.asJavaCollection(HelpIndex.lookup(request.getText()))) {
            if (!response.addResult(() -> { selectPath(result.path()); }, result.displayName(), result.displayHint(), Collections.emptyList())) {
                break;
            }
        }
    }

    private void selectPath(String path) {
        CgsuiteHelpTopComponent component = (CgsuiteHelpTopComponent) CgsuiteHelpTopComponent.getRegistry().getActivated();
        component.navigateTo(path);
    }
    
}
