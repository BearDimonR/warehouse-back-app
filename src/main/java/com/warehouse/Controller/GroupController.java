package com.warehouse.Controller;

import com.warehouse.Model.Group;
import com.warehouse.Service.GroupService;
import org.apache.logging.log4j.LogManager;

public class GroupController extends AbstractController<Group> {

    public GroupController() {
        super(Group.class);
        getPermission = "group_read";
        updatePermission = "group_edit";
        createPermission = "group_create";
        deletePermission = "group_delete";
        service = GroupService.getInstance();

        logger = LogManager.getLogger(GroupController.class);
    }
}
