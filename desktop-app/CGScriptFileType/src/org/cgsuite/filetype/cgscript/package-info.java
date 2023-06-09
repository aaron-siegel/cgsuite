/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

@TemplateRegistrations({
    @TemplateRegistration(
        folder = "CGSuite",
        position = 100,
        content = "ClassTemplate.cgs",
        requireProject = false,
        displayName = "Class",
        scriptEngine="freemarker",
        iconBase="org/cgsuite/filetype/cgscript/thermograph-16x16.png"
    ),
    @TemplateRegistration(
        folder = "CGSuite",
        position = 200,
        content = "ScriptTemplate.cgs",
        requireProject = false,
        displayName = "Script",
        scriptEngine="freemarker",
        iconBase="org/cgsuite/filetype/cgscript/thermograph-16x16.png"
    )
})

package org.cgsuite.filetype.cgscript;

import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.templates.TemplateRegistrations;
