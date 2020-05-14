import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { DatTableDataSource, DatTableItem } from './dat-table-datasource';

@Component({
  selector: 'app-dat-table',
  templateUrl: './dat-table.component.html',
  styleUrls: ['./dat-table.component.css']
})
export class DatTableComponent implements AfterViewInit, OnInit {
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatTable) table: MatTable<DatTableItem>;
  dataSource: DatTableDataSource;

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'fileNumber', 'seller', 'discription', 'address', 'auctionDate', 'auctionTime', 'estimate'];

  ngOnInit() {
    this.dataSource = new DatTableDataSource();
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.table.dataSource = this.dataSource;
  }
}
