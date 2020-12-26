import { HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ConversionService } from '../services/conversion.service';

@Component({
  selector: 'app-conversion',
  templateUrl: './conversion.component.html',
  styleUrls: ['./conversion.component.scss']
})
export class ConversionComponent implements OnInit {

  choosenFile: string;
  infoText: string;

  constructor(private conversionService: ConversionService) { }

  ngOnInit(): void {
      this.choosenFile = "Choose file";
      this.infoText = "";
  }

  public uploadImage(event: any) {
    let uploadedFile = event.srcElement.files[0];
    this.choosenFile = uploadedFile.name;
    const params = new HttpParams().set("imageFile", uploadedFile); // Set http params
    this.conversionService.uploadFile(params).subscribe((data: any) => {
        this.infoText = "Upload successful!";
    }, (error: any) => {
        if (error.value().includes("Cookie missing!")) {
            alert("Please accept Cookies to use the image convertion function.");
        } else {
            alert("Upload failed: " + error.value());
        }
        this.infoText = "";
    });
  }

  public downloadImage(event: any) {
    this.conversionService.downloadFile().subscribe((data: any) => {
        this.infoText = "Download successful!";
    }, (error: any) => {
        alert("Download ended with an error: " + error.value());
        this.infoText = "";
    });
  }

  // File appears on select
/*$("#inputFile").on("change", function () {
    var cookie = Cookies.get("user_session");
    if (cookie === undefined) {
      alert("Please accept Cookies to use the image convertion function.");
      return;
    }
    window.fileName = $(this).val().split("\\").pop();
    $(this)
      .siblings(".custom-file-label")
      .addClass("selected")
      .html(window.fileName);
    var form = $("#uploadForm");
    form.submit();
  });*/

}
