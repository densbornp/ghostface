import { HttpHeaders, HttpParams } from '@angular/common/http';
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
  INFO_FINISHED: "Finished!";
  INFO_DOWNLOAD: "Download successful!";
  INFO_UPLOAD: "Upload successful!";
  downloadBtnDisabled = true;

  constructor(private conversionService: ConversionService) { }

  ngOnInit(): void {
      this.choosenFile = "Choose file";
      this.infoText = null;
  }

  public uploadImage(event: any) {
    let uploadedFile = event.srcElement.files[0];
    this.choosenFile = uploadedFile.name;
    let formData = new FormData();
    formData.append("imageFile", uploadedFile);
    // TODO subscribe gets not triggered
    this.conversionService.uploadFile(formData).subscribe((data: any) => {}, (error: any) => {
        if (error.value().includes("Cookie missing!")) {
            alert("Please accept Cookies to use the image convertion function.");
        } else {
            alert("Upload failed: " + error.value());
        }
        this.infoText = null;
    }, () => {
        this.downloadBtnDisabled = false;
        this.infoText = this.INFO_UPLOAD;
    });
  }

  public downloadImage(event: any) {
    this.conversionService.downloadFile().subscribe((data: any) => {}, (error: any) => {
        alert("Download ended with an error: " + error.value());
        this.infoText = null;
    }, () => {this.infoText = this.INFO_DOWNLOAD;});
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
