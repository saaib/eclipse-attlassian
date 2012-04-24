/*
 * $Header: /home/projects/jaxen/scm/jaxen/src/java/main/org/jaxen/util/AncestorAxisIterator.java,v 1.14 2006/11/09 18:20:12 elharo Exp $
 * $Revision: 1.14 $
 * $Date: 2006/11/09 18:20:12 $
 *
 * ====================================================================
 *
 * Copyright 2000-2002 bob mcwhirter & James Strachan.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 * 
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 * 
 *   * Neither the name of the Jaxen Project nor the names of its
 *     contributors may be used to endorse or promote products derived 
 *     from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 * This software consists of voluntary contributions made by many 
 * individuals on behalf of the Jaxen Project and was originally 
 * created by bob mcwhirter <bob@werken.com> and 
 * James Strachan <jstrachan@apache.org>.  For more information on the 
 * Jaxen Project, please see <http://www.jaxen.org/>.
 * 
 * $Id: AncestorAxisIterator.java,v 1.14 2006/11/09 18:20:12 elharo Exp $
 */



package org.jaxen.util;


import org.jaxen.Navigator;

/**
 * Represents the XPath <code>ancestor</code> axis. 
 * The "<code>ancestor</code> axis contains the ancestors of the context node; 
 * the ancestors of the context node consist of the parent of context node and 
 * the parent's parent and so on; thus, the ancestor axis will always include 
 * the root node, unless the context node is the root node."
 * 
 * @version 1.2b12
 */
public class AncestorAxisIterator extends AncestorOrSelfAxisIterator
{

    /**
     * Create a new ancestor axis iterator.
     * 
     * @param contextNode the node to start from
     * @param navigator the object model specific navigator
     */
    public AncestorAxisIterator(Object contextNode,
                                Navigator navigator)
    {
        super( contextNode,
               navigator );
        next();
    }
    
}
