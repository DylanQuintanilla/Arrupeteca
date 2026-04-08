package com.arrupeteca.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Base class for controller integration tests using MockMvc.
 */
public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

}
