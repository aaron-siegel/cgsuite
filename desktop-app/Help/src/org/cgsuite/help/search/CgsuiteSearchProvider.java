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
            boolean success = response.addResult(
                () -> { CgsuiteHelpTopComponent.openAndNavigateTo(result.path()); },
                result.displayName(),
                result.displayHint(),
                Collections.emptyList()
            );
            if (!success)
                break;
        }
    }
    
}
