import {useEffect, useState} from "react";
import * as OrderApi from "../../../api/OrderApi";
import {DataGrid, getGridStringOperators, getGridSingleSelectOperators} from "@mui/x-data-grid";
import classes from "./OrdersTable.module.css";
import './Fixes.css';
import {Link} from "react-router-dom";

export function OrdersTable(props) {
    let [rows, setRows] = useState([])
    let [page, setPage] = useState(0)
    let [pageSize, setPageSize] = useState(100)
    let [sortModel, setSortModel] = useState([])
    let [filterModel, setFilterModel] = useState({items: []})
    let [filter, setFilter] = useState({filtering: [], ordering: [], page: page, pageSize: pageSize})


    let requestPage = (filter) => {
        return OrderApi.orders({
            filter: filter
        }).then((result) => {
            return result.orders.map(order => {
                return {
                    id: order.requestId,
                    orderId: order.requestId,
                    pickup: JSON.stringify(order.recordedOrigin, null, 2),
                    dropoff: JSON.stringify(order.recordedDestination, null, 2),
                    orderedAt: order.requestedAt,
                    completedAt: order.completedAt,
                    servingSessionId: order.servingSessionId,
                    status: order.status
                }
            })
        })
    }

    useEffect(() => {
        setFilter({...filter, page: page})
    }, [page])

    useEffect(() => {
        let ordering = (sortModel.length !== 0) ? [{
            fieldName: sortModel[0].field,
            ascending: sortModel[0].sort === 'asc'
        }] : []
        setFilter({...filter, ordering: ordering})
    }, [sortModel])

    useEffect(() => {
        let isAnyOfItems = filterModel.items.filter(item => item.operatorValue === 'isAnyOf')
        let filters = isAnyOfItems.map(item => {
            return {
                fieldName: item.columnField,
                values: item.value || []
            }
        })
        setFilter({...filter, filtering: filters || []})
    }, [filterModel])

    useEffect(() => {
        requestPage(filter).then((rows) => {
            setRows(rows)
        })
    }, [filter])

    const stringOperators = getGridStringOperators().filter(({value}) => ['isAnyOf'].includes(value))
    const singleSelectOperators = getGridSingleSelectOperators().filter(({value}) => ['isAnyOf'].includes(value))

    const columns = [
        {
            field: 'orderId',
            headerName: 'ID',
            minWidth: 50,
            flex: true,
            headerClassName: classes.GridHeader,
            renderCell: cell => {
                return (<div className={classes.GridCell}>
                    <Link to={`/order/${cell.value}`} className={classes.Link}>{cell.value}</Link>
                </div>)
            },
            filterOperators: stringOperators
        },
        {
            field: 'orderedAt',
            headerName: '?????????? ????????????????',
            minWidth: 200,
            flex: true,
            filterable: false,
            headerClassName: classes.GridHeader,
        },
        {
            field: 'completedAt',
            headerName: '?????????? ????????????????????',
            minWidth: 200,
            flex: true,
            filterable: false,
            headerClassName: classes.GridHeader,
        },
        {
            field: 'servingSessionId',
            headerName: '????????????',
            minWidth: 50,
            flex: true,
            headerClassName: classes.GridHeader,
            renderCell: cell => {
                return (<div className={classes.GridCell}>
                    <Link to={`/session/${cell.value}`} className={classes.Link}>{cell.value}</Link>
                </div>)
            },
            filterOperators: stringOperators
        },
        {
            field: 'status',
            headerName: '????????????',
            minWidth: 150,
            flex: true,
            headerClassName: classes.GridHeader,
            type: 'singleSelect',
            valueOptions: ['SERVICE_PENDING', 'SERVICE_DENIED', 'ACCEPTED', 'PICKUP_PENDING', 'SERVING', 'SERVED', 'CANCELLED'],
            filterOperators: singleSelectOperators
        },
    ];

    return (
        <div className={classes.Wrapper}>
            <DataGrid className={classes.DataGrid}
                      rows={rows}
                      columns={columns}
                      density="compact"
                      filterMode="server"
                      sortingMode="server"
                      rowsPerPageOptions={[10]}
                      onPageChange={page => setPage(page)}
                      hideFooter={true}
                      onSortModelChange={sortModel => setSortModel(sortModel)}
                      filterModel={filterModel}
                      onFilterModelChange={filterModel => setFilterModel(filterModel)}
                      sx={{
                          '.MuiDataGrid-columnSeparator': {
                              display: 'none',
                          },
                          '&.MuiDataGrid-root': {
                              border: 'none',
                          },
                          '.MuiDataGrid-columnHeaderTitle': {
                              fontWeight: "bold",
                              marginLeft: "30px"
                          },
                          '.MuiDataGrid-cellContent': {
                              width: "100%",
                              height: "100%",
                              display: "flex",
                              alignItems: "flex-start",
                              paddingLeft: "30px",
                          }
                      }}
            />
            <div className={classes.Footer}>
                <div>
                    {page * pageSize} - {page * pageSize + rows.length}
                </div>
                <button className={classes.IconButton} style={{background: "url(icons/prev-page-button.svg) no-repeat center/50%"}}
                        disabled={page <= 0}
                        onClick={() => setPage(page - 1)}>
                </button>
                <button className={classes.IconButton} style={{background: "url(icons/next-page-button.svg) no-repeat center/50%"}}
                        disabled={rows.length < pageSize}
                        onClick={() => setPage(page + 1)}>
                </button>
            </div>
        </div>
    )
}