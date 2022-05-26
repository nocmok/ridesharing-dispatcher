import {useEffect, useState} from "react";
import * as SessionApi from "../../../api/SessionApi";
import {DataGrid, getGridStringOperators, getGridSingleSelectOperators} from "@mui/x-data-grid";
import classes from "./SessionsTable.module.css";
import './Fixes.css';

export function SessionsTable(props) {
    let [rows, setRows] = useState([])
    let [page, setPage] = useState(0)
    let [pageSize, setPageSize] = useState(100)
    let [sortModel, setSortModel] = useState([])
    let [filterModel, setFilterModel] = useState({items: []})
    let [filter, setFilter] = useState({filtering: [], ordering: [], page: page, pageSize: pageSize})


    let requestPage = (filter) => {
        return SessionApi.sessions({
            filter: filter
        }).then((result) => {
            return result.sessions.map(session => {
                return {
                    id: session.sessionId,
                    sessionId: session.sessionId,
                    capacity: session.capacity,
                    residualCapacity: session.residualCapacity
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

    const columns = [
        {
            field: 'sessionId',
            headerName: 'ID',
            minWidth: 50,
            flex: true,
            headerClassName: classes.GridHeader,
            filterOperators: stringOperators
        },
        {
            field: 'capacity',
            headerName: 'Вместимость',
            minWidth: 50,
            flex: true,
            headerClassName: classes.GridHeader,
            filterOperators: stringOperators
        },
        {
            field: 'residualCapacity',
            headerName: 'Остаточная вместимость',
            minWidth: 50,
            flex: true,
            headerClassName: classes.GridHeader,
            filterOperators: stringOperators
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