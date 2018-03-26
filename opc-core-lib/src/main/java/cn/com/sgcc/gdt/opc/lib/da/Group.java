package cn.com.sgcc.gdt.opc.lib.da;

import cn.com.sgcc.gdt.opc.core.dcom.common.EventHandler;
import cn.com.sgcc.gdt.opc.core.dcom.common.bean.KeyedResult;
import cn.com.sgcc.gdt.opc.core.dcom.common.bean.KeyedResultSet;
import cn.com.sgcc.gdt.opc.core.dcom.common.bean.Result;
import cn.com.sgcc.gdt.opc.core.dcom.common.bean.ResultSet;
import cn.com.sgcc.gdt.opc.core.dcom.da.IOPCDataCallback;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.OpcDatasource;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.OpcItemDef;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.OpcItemResult;
import cn.com.sgcc.gdt.opc.core.dcom.da.bean.OpcItemState;
import cn.com.sgcc.gdt.opc.core.dcom.da.impl.OPCAsyncIO2;
import cn.com.sgcc.gdt.opc.core.dcom.da.impl.OPCGroupStateMgt;
import cn.com.sgcc.gdt.opc.core.dcom.da.impl.OPCItemMgt;
import cn.com.sgcc.gdt.opc.core.dcom.da.impl.OPCSyncIO;
import cn.com.sgcc.gdt.opc.lib.da.exception.AddFailedException;
import lombok.extern.slf4j.Slf4j;
import org.jinterop.dcom.common.JIException;

import java.net.UnknownHostException;
import java.util.*;

/**
 * 组
 * @author ck.yang
 */
@Slf4j
public class Group {

    private static Random random = new Random();

    private Server server = null;

    private final int serverHandle;

    private OPCGroupStateMgt group = null;

    private OPCItemMgt items = null;

    private OPCSyncIO syncIO = null;

    private final Map<String, Integer> itemHandleMap = new HashMap<>();

    private final Map<Integer, Item> itemMap = new HashMap<>();

    private final Map<Integer, Item> itemClientMap = new HashMap<>();

    Group(final Server server, final int serverHandle, final OPCGroupStateMgt group) throws IllegalArgumentException, UnknownHostException, JIException {
        log.info("创建组：{} ", group);
        this.server = server;
        this.serverHandle = serverHandle;
        this.group = group;
        this.items = group.getItemManagement();
        this.syncIO = group.getSyncIO();
    }

    public void setActive(final boolean state) throws JIException {
        this.group.setState(null, state, null, null, null, null);
    }

    /**
     * remove the group from the server
     *
     * @throws JIException
     */
    public void remove() throws JIException {
        this.server.removeGroup(this, true);
    }

    public boolean isActive() throws JIException {
        return this.group.getState().isActive();
    }

    /**
     * Get the group name from the server
     *
     * @return The group name fetched from the server
     * @throws JIException
     */
    public String getName() throws JIException {
        return this.group.getState().getName();
    }

    /**
     * Change the group name
     *
     * @param name the new name of the group
     * @throws JIException
     */
    public void setName(final String name) throws JIException {
        this.group.setName(name);
    }

    /**
     * Add a single item. Actually calls {@link #addItems(String[])} with only
     * one paraemter
     *
     * @param item The item to add
     * @return The added item
     * @throws JIException        The add operation failed
     * @throws AddFailedException The item was not added due to an error
     */
    public Item addItem(final String item) throws JIException, AddFailedException {
        Map<String, Item> items = addItems(item);
        return items.get(item);
    }

    /**
     * Validate item ids and get additional information to them.
     * <br>
     * According to the OPC specification you should first <em>validate</em>
     * the items and the <em>add</em> them. The spec also says that when a server
     * lets the item pass validation it must also let them pass the add operation.
     *
     * @param items The items to validate
     * @return A result map of item id to result information (including error code).
     * @throws JIException
     */
    public synchronized Map<String, Result<OpcItemResult>> validateItems(final String... items) throws JIException {
        OpcItemDef[] defs = new OpcItemDef[items.length];
        for (int i = 0; i < items.length; i++) {
            defs[i] = new OpcItemDef();
            defs[i].setItemID(items[i]);
        }

        KeyedResultSet<OpcItemDef, OpcItemResult> result = this.items.validate(defs);

        Map<String, Result<OpcItemResult>> resultMap = new HashMap<>();
        for (KeyedResult<OpcItemDef, OpcItemResult> resultEntry : result) {
            resultMap.put(resultEntry.getKey().getItemID(), new Result<>(resultEntry.getValue(), resultEntry.getErrorCode()));
        }

        return resultMap;
    }

    /**
     * Add new items to the group
     *
     * @param items The items (by string id) to add
     * @return A result map of id to item object
     * @throws JIException        The add operation completely failed. No item was added.
     * @throws AddFailedException If one or more item could not be added. Item without error where added.
     */
    public synchronized Map<String, Item> addItems(final String... items) throws JIException, AddFailedException {
        // Find which items we already have
        Map<String, Integer> handles = findItems(items);

        List<Integer> foundItems = new ArrayList<>(items.length);
        List<String> missingItems = new ArrayList<>();

        // separate missing items from the found ones
        for (Map.Entry<String, Integer> entry : handles.entrySet()) {
            if (entry.getValue() == null) {
                missingItems.add(entry.getKey());
            } else {
                foundItems.add(entry.getValue());
            }
        }

        // now fetch missing items from OPC server
        Set<Integer> newClientHandles = new HashSet<>();
        OpcItemDef[] itemDef = new OpcItemDef[missingItems.size()];
        for (int i = 0; i < missingItems.size(); i++) {
            OpcItemDef def = new OpcItemDef();
            def.setItemID(missingItems.get(i));
            def.setActive(true);

            Integer clientHandle;
            do {
                clientHandle = random.nextInt();
            } while (this.itemClientMap.containsKey(clientHandle) || newClientHandles.contains(clientHandle));
            newClientHandles.add(clientHandle);
            def.setClientHandle(clientHandle);

            itemDef[i] = def;
        }

        // check the result and add new items
        Map<String, Integer> failedItems = new HashMap<>();
        KeyedResultSet<OpcItemDef, OpcItemResult> result = this.items.add(itemDef);
        int i = 0;
        for (KeyedResult<OpcItemDef, OpcItemResult> entry : result) {
            if (entry.getErrorCode() == 0) {
                Item item = new Item(this, entry.getValue().getServerHandle(), itemDef[i].getClientHandle(), entry.getKey().getItemID());
                addItem(item);
                foundItems.add(item.getServerHandle());
            } else {
                failedItems.put(entry.getKey().getItemID(), entry.getErrorCode());
            }
            i++;
        }

        // if we have failed items then throw an exception with the result
        if (failedItems.size() != 0) {
            throw new AddFailedException(failedItems, findItems(foundItems));
        }

        // simply return the result in case of success
        return findItems(foundItems);
    }

    private synchronized void addItem(final Item item) {
        log.debug(String.format("Adding item: '%s', %d", item.getId(), item.getServerHandle()));

        this.itemHandleMap.put(item.getId(), item.getServerHandle());
        this.itemMap.put(item.getServerHandle(), item);
        this.itemClientMap.put(item.getClientHandle(), item);
    }

    private synchronized void removeItem(final Item item) {
        this.itemHandleMap.remove(item.getId());
        this.itemMap.remove(item.getServerHandle());
        this.itemClientMap.remove(item.getClientHandle());
    }

    protected Item getItemByOPCItemId(final String opcItemId) {
        Integer serverHandle = this.itemHandleMap.get(opcItemId);
        if (serverHandle == null) {
            log.debug(String.format("Failed to locate item with id '%s'", opcItemId));
            return null;
        }
        log.debug(String.format("Item '%s' has server id '%d'", opcItemId, serverHandle));
        return this.itemMap.get(serverHandle);
    }

    private synchronized Map<String, Integer> findItems(final String[] items) {
        Map<String, Integer> data = new HashMap<>();

        for (int i = 0; i < items.length; i++) {
            data.put(items[i], this.itemHandleMap.get(items[i]));
        }

        return data;
    }

    private synchronized Map<String, Item> findItems(final Collection<Integer> handles) {
        Map<String, Item> itemMap = new HashMap<>();
        for (Integer i : handles) {
            Item item = this.itemMap.get(i);
            if (item != null) {
                itemMap.put(item.getId(), item);
            }
        }
        return itemMap;
    }

    protected void checkItems(final Item[] items) {
        for (Item item : items) {
            if (item.getGroup() != this) {
                throw new IllegalArgumentException("Item does not belong to this group");
            }
        }
    }

    public void setActive(final boolean state, final Item... items) throws JIException {
        checkItems(items);

        Integer[] handles = new Integer[items.length];
        for (int i = 0; i < items.length; i++) {
            handles[i] = items[i].getServerHandle();
        }

        this.items.setActiveState(state, handles);
    }

    protected Integer[] getServerHandles(final Item[] items) {
        checkItems(items);

        Integer[] handles = new Integer[items.length];

        for (int i = 0; i < items.length; i++) {
            handles[i] = items[i].getServerHandle();
        }

        return handles;
    }

    public synchronized Map<Item, Integer> write(final WriteRequest... requests) throws JIException {
        Item[] items = new Item[requests.length];

        for (int i = 0; i < requests.length; i++) {
            items[i] = requests[i].getItem();
        }

        Integer[] handles = getServerHandles(items);

        cn.com.sgcc.gdt.opc.core.dcom.da.bean.WriteRequest[] wr = new cn.com.sgcc.gdt.opc.core.dcom.da.bean.WriteRequest[items.length];
        for (int i = 0; i < items.length; i++) {
            wr[i] = new cn.com.sgcc.gdt.opc.core.dcom.da.bean.WriteRequest(handles[i], requests[i].getValue());
        }

        ResultSet<cn.com.sgcc.gdt.opc.core.dcom.da.bean.WriteRequest> resultSet = this.syncIO.write(wr);

        Map<Item, Integer> result = new HashMap<>();
        for (int i = 0; i < requests.length; i++) {
            Result<cn.com.sgcc.gdt.opc.core.dcom.da.bean.WriteRequest> entry = resultSet.get(i);
            result.put(requests[i].getItem(), entry.getErrorCode());
        }

        return result;
    }

    public synchronized Map<Item, ItemState> read(final boolean device, final Item... items) throws JIException {
        Integer[] handles = getServerHandles(items);

        KeyedResultSet<Integer, OpcItemState> states = this.syncIO.read(device ? OpcDatasource.OPC_DS_DEVICE : OpcDatasource.OPC_DS_CACHE, handles);

        Map<Item, ItemState> data = new HashMap<>();
        for (KeyedResult<Integer, OpcItemState> entry : states) {
            Item item = this.itemMap.get(entry.getKey());
            ItemState state = new ItemState(entry.getErrorCode(), entry.getValue().getValue(), entry.getValue().getTimestamp().asCalendar(), entry.getValue().getQuality());
            data.put(item, state);
        }
        return data;
    }

    public Server getServer() {
        return this.server;
    }

    public synchronized void clear() throws JIException {
        Integer[] handles = this.itemMap.keySet().toArray(new Integer[0]);
        try {
            this.items.remove(handles);
        } finally {
            // in any case clear our maps
            this.itemHandleMap.clear();
            this.itemMap.clear();
            this.itemClientMap.clear();
        }
    }

    public synchronized OPCAsyncIO2 getAsyncIO20() {
        return this.group.getAsyncIO2();
    }

    public synchronized EventHandler attach(final IOPCDataCallback dataCallback) throws JIException {
        return this.group.attach(dataCallback);
    }

    public Item findItemByClientHandle(final int clientHandle) {
        return this.itemClientMap.get(clientHandle);
    }

    public int getServerHandle() {
        return this.serverHandle;
    }

    public synchronized void removeItem(final String opcItemId) throws IllegalArgumentException, UnknownHostException, JIException {
        log.debug(String.format("Removing item '%s'", opcItemId));
        Item item = getItemByOPCItemId(opcItemId);
        if (item != null) {
            this.group.getItemManagement().remove(item.getServerHandle());
            removeItem(item);
            log.debug(String.format("Removed item '%s'", opcItemId));
        } else {
            log.warn(String.format("Unable to find item '%s'", opcItemId));
        }
    }

}
