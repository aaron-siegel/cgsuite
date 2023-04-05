package org.cgsuite.help

import netscape.javascript.JSObject

object HelpUtil {

  def setJSMember(window: Object, name: String, member: Object): Unit = {
    window.asInstanceOf[JSObject].setMember("cgsuite", member)
  }

}
